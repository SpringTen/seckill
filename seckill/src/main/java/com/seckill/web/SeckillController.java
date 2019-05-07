package com.seckill.web;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.Seckill;
import com.seckill.enums.SeckillStateEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

/**
 * @Class SeckillController
 * @Package com.seckill.web
 * @Author lizhanzhan
 * @Date 2019/5/5 15:22
 * @Motto talk is cheap,show me the code
 */
@Controller
@RequestMapping("/seckill") //模块    /模块/{}/细分   /模块/list·   /seckill/list
public class SeckillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        //list.jsp + model = ModelAndView
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    //Ajax返回json
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST,
                    produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result = null;
        try {
            Exposer exposer = seckillService.exposeSeckillUrl(seckillId);
            result = new SeckillResult<>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillResult<>(false, e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST,
                    produces = "application/json;charset=UTF-8")
    public SeckillResult<SeckillExecution> execute(
            @PathVariable("seckillId")Long seckillId,
            @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false) Long userPhone){
        if (userPhone == null){
            return new SeckillResult<>(false, "未注册");
        }
        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
            return new SeckillResult<>(true, execution);
        } catch (RepeatKillException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<>(true, execution);
        } catch (SeckillCloseException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<>(true, execution);
        }
    }

    //获取服务器当前的系统时间
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        return new SeckillResult<>(true, new Date().getTime());
    }
}
