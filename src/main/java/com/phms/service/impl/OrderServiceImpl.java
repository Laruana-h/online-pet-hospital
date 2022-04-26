package com.phms.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.phms.mapper.OrderMapper;
import com.phms.model.MMGridPageVoBean;
import com.phms.pojo.Appointment;
import com.phms.pojo.Order;
import com.phms.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-04-22
 */
@Service
public class OrderServiceImpl implements IOrderService
{
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public Order selectOrderById(Long id)
    {
        return orderMapper.selectOrderById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param order 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public Object selectOrderList(Order order)
    {
        int size = 0;
        // 计算分页
        Integer begin = (order.getPage() - 1) * order.getLimit();
        order.setPage(begin);

        List<Order> rows = new ArrayList<>();
        try {
            rows = orderMapper.selectOrderList(order);
            size = orderMapper.countOrderList(order);
        } catch (Exception e) {
        }
        MMGridPageVoBean<Order> vo = new MMGridPageVoBean<>();
        vo.setTotal(size);
        vo.setRows(rows);

        return vo;
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param order 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertOrder(Order order)
    {
        return orderMapper.insertOrder(order);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param order 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateOrder(Order order)
    {
        orderMapper.updateOrder(order);
        return 1;
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteOrderByIds(Long[] ids)
    {
        return orderMapper.deleteOrderByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteOrderById(Long id)
    {
        return orderMapper.deleteOrderById(id);
    }
}
