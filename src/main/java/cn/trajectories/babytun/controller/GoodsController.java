package cn.trajectories.babytun.controller;

import cn.trajectories.babytun.entity.Goods;
import cn.trajectories.babytun.service.GoodsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;


@Controller
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @GetMapping("/good")
    @ResponseBody
    @ApiOperation(value = "商品信息展示页面")
    public ModelAndView showGoods(@ApiParam(value = "商品id") Long gid){
        //System.out.println("gid" + gid);
        ModelAndView mav = new ModelAndView("goods");
        mav.addObject("goods",goodsService.getGoods(gid));
        mav.addObject("covers",goodsService.findCovers(gid));
        mav.addObject("details",goodsService.findDetails(gid));
        mav.addObject("params",goodsService.findParams(gid));
        return mav;
    }
}
