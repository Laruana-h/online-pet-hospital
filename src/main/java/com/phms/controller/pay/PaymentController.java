package com.phms.controller.pay;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.phms.config.PaypalPaymentIntent;
import com.phms.config.PaypalPaymentMethod;
import com.phms.model.PayData;
import com.phms.pojo.Order;
import com.phms.pojo.User;
import com.phms.service.IOrderService;
import com.phms.service.PaypalService;
import com.phms.service.impl.OrderServiceImpl;
import com.phms.utils.URLUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
public class PaymentController {

    public static final String PAYPAL_SUCCESS_URL = "pay/success";
    public static final String PAYPAL_CANCEL_URL = "pay/cancel";

    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private IOrderService orderService;

    @Autowired
    private PaypalService paypalService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(){
        return "index";
    }

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
                paySuccess(payment,httpServletRequest);
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
    public boolean paySuccess(Payment payment, HttpServletRequest request) {
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
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        Order order = new Order();
        order.setGoodsName(payData.getTitle());
        order.setPrice(payData.getPrice().toString());
        order.setBuyTime(new Date());
        order.setUserId(user.getId().longValue());
        order.setUserName(user.getName());
        orderService.insertOrder(order);

        return true;
    }


}
