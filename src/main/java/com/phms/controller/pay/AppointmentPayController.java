package com.phms.controller.pay;

import com.alibaba.fastjson.JSON;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.phms.config.PaypalPaymentIntent;
import com.phms.config.PaypalPaymentMethod;
import com.phms.model.PayData;
import com.phms.pojo.Appointment;
import com.phms.pojo.Order;
import com.phms.pojo.User;
import com.phms.service.AppointmentService;
import com.phms.service.IOrderService;
import com.phms.service.PaypalService;
import com.phms.utils.URLUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author LinChaoFan
 * @Date 2022/4/24 15:31
 * @Version 1.0
 */
@Controller
@RequestMapping("/apply/pay")
public class AppointmentPayController {
    public static final String PAYPAL_SUCCESS_URL = "apply/pay/success";
    public static final String PAYPAL_CANCEL_URL = "apply/pay/cancel";

    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private IOrderService orderService;

    @Autowired
    private PaypalService paypalService;
    @Autowired
    private AppointmentService appointmentService;

    @RequestMapping(method = RequestMethod.POST, value = "/pay")
    @ResponseBody
    public Object pay(HttpServletRequest request, @RequestBody PayData payData){
        String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
        String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
        Double p = payData.getPrice().doubleValue();

        try {
            Payment payment = paypalService.createPayment(
                    payData,
                    p,
                    "USD",
                    PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale,
                    "payment description",
                    cancelUrl,
                    successUrl);
            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return JSON.toJSONString(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"+e.getMessage());
        }
        return "redirect:/";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cancel")
    public String cancelPay(){
        return "/user/pay/cancel";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/success")
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId,HttpServletRequest httpServletRequest){
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                applyPaySuccess(payment,httpServletRequest);
                return "/user/pay/success";
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return "redirect:/";
    }



    /**
     * 支付成功的处理逻辑
     *
     * @param payment
     *            贝宝的参数信息
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public boolean applyPaySuccess(Payment payment, HttpServletRequest request) {
        // 交易状态
        String status = "";
        for(Links links : payment.getLinks()){
            if(links.getRel().equals("approval_url")){
                status=links.getRel();
                break;
            }
            status=links.getRel();
        }
        // 商户订单号
        String out_trade_no = payment.getTransactions().get(0).getInvoiceNumber();
        // paypal交易号
        String trade_no = payment.getId();
        // 附加json字段，取出下单时存放的业务信息
        String extra_common_param = payment.getTransactions().get(0).getCustom();
        PayData payData = JSON.parseObject(extra_common_param,PayData.class);

        Appointment appointment = appointmentService.getById(Long.valueOf(payData.getId()));
        appointment.setPayStatus(1);
        appointmentService.update(appointment);

        return true;
    }

}
