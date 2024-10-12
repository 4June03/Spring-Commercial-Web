package com.example.Ecommercial_project.Shoping.website.controller;

import com.example.Ecommercial_project.Shoping.website.model.Category;
import com.example.Ecommercial_project.Shoping.website.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private CategoryService categoryService;

    @Autowired
    public AdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String index(){
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(){
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String loadAddCategory(Model model){
        List<Category> categoryList = categoryService.getAllCategory();
        model.addAttribute("categories",categoryList);
        return "admin/category";
    }


    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        //kiểm tra file
        //Set file
       String imageName = file!=null? file.getOriginalFilename():"default.jpg";
       category.setImageName(imageName);

        //kiểm tra nếu category đã tồn tại thì đưa ra tbao lỗi qua session
        boolean existCategory = categoryService.existCategory(category.getName());
        if(existCategory){
            session.setAttribute("errorMsg", "Category name already exists!");
        }else{
           Category saveCategory =  categoryService.saveCategory(category);
           if(ObjectUtils.isEmpty(saveCategory)){
               session.setAttribute("errorMsg", "Category is not saved!");
           }else{

              //Dòng này lấy đường dẫn đến thư mục static/img trong classpath của dự án.
               //getFile(): Phương thức này trả về đối tượng File đại diện cho thư mục static/img trong hệ thống file.
             File saveFile =  new ClassPathResource("static/img").getFile();
             //Tạo ra đường dẫn tới vị trí lưu hình ảnh, bao gồm tên file của hình ảnh.
               // separator đại diện cho dấu '/'
              Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_images"+File.separator+file.getOriginalFilename());
               System.out.println(path);
                //Copy nội dung của file tải lên từ client và lưu nó vào đường dẫn path vừa tạo.
               Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                //Đoạn mã này nhận một tệp hình ảnh tải lên từ client,
               // tạo ra một đường dẫn trong hệ thống file của ứng dụng
               // (cụ thể là trong thư mục static/img/category_images),
               // và sau đó lưu tệp vào vị trí đó. Nếu tệp đã tồn tại, nó sẽ bị ghi đè.


               session.setAttribute("succMsg", "Category is successfully saved!");
           }

        }
        return "redirect:/admin/category";
    }


    @GetMapping("/deleteCategory/{id}")
    public String deleteCategoryById(@PathVariable int id, HttpSession session){
        Boolean isDeleted = categoryService.deleteCategory(id);
        if (isDeleted){
            session.setAttribute("succMsg", "Delete category success");
        }else{
            session.setAttribute("errorMsg", "Delete category failed");
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model){

        model.addAttribute("category",categoryService.findCategoryById(id));

        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file")MultipartFile file, HttpSession session){
        Category oldCategory = categoryService.findCategoryById(category.getId());
        if(!ObjectUtils.isEmpty(oldCategory)){
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            //Nếu file mới lỗi thì lấy ảnh file cũ
           String imageName =  file!=null?file.getOriginalFilename():oldCategory.getImageName();
            oldCategory.setImageName(imageName);

        }

        Category updatedCategory =  categoryService.saveCategory(oldCategory);
        if(!ObjectUtils.isEmpty(updatedCategory)){
            session.setAttribute("succMsg", "Updated category success");
        }else{
            session.setAttribute("errorMsg", "Failed to update category");
        }

        return "redirect:/admin/loadEditCategory/"+updatedCategory.getId();
    }
}
