package com.LTY.controller;

import com.LTY.domin.ResponseResult;
import com.LTY.domin.entity.Category;
import com.LTY.domin.vo.CategoryVo;
import com.LTY.domin.vo.ExcelCategoryVo;
import com.LTY.domin.vo.PageVo;
import com.LTY.enums.AppHttpCodeEnum;
import com.LTY.service.CategoryService;
import com.LTY.utils.BeanCopyUtils;
import com.LTY.utils.WebUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 刘泰源
 * @version 1.8
 */
@RestController
@RequestMapping("/content/category")
public class CategoryController {
   @Autowired
   private CategoryService categoryService;
    @GetMapping("/list")
    public ResponseResult list(Integer pageNum,Integer pageSize,String name,String status){
        PageVo pageVo = categoryService.selectAllCategory(pageNum,pageSize,name,status);
        return ResponseResult.okResult(pageVo);
    }
    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory(){
        List<CategoryVo> list = categoryService.listAllCategory();
        return ResponseResult.okResult(list);
    }
    @PutMapping
    public ResponseResult edit(@RequestBody Category category){
        categoryService.updateById(category);
        return ResponseResult.okResult();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseResult remove(@PathVariable(value = "id")Long id){
        categoryService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping(value = "/{id}")
    public ResponseResult getInfo(@PathVariable(value = "id")Long id){
        Category category = categoryService.getById(id);
        return ResponseResult.okResult(category);
    }
    @PostMapping
    public ResponseResult add(@RequestBody Category category){
        categoryService.save(category);
        return ResponseResult.okResult();
    }
    @PreAuthorize("@ps.hasPermission('content:category:export')")
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        try {
            //设置下载文件的请求头
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            //获取需要导出的数据
            List<Category> categoryVos = categoryService.list();

            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryVos, ExcelCategoryVo.class);
            //把数据写入到Excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(excelCategoryVos);

        } catch (Exception e) {
            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }
}
