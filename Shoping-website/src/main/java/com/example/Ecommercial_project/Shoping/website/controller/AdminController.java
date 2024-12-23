package com.example.Ecommercial_project.Shoping.website.controller;

import com.example.Ecommercial_project.Shoping.website.model.Category;
import com.example.Ecommercial_project.Shoping.website.model.Product;
import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import com.example.Ecommercial_project.Shoping.website.service.CategoryService;
import com.example.Ecommercial_project.Shoping.website.service.ProductService;
import com.example.Ecommercial_project.Shoping.website.service.UserService;
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
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private CategoryService categoryService;
    private ProductService productService;
    private UserService userService;


    @Autowired
    public AdminController(CategoryService categoryService, ProductService productService, UserService userService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model model){
        if(p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user",userDtls);
        }

        //Lấy danh sách category để truyền vào navbar
        List<Category> activeCategories = categoryService.getAllActiveCategory();
        model.addAttribute("categories",activeCategories);
    }


    @GetMapping("/")
    public String index(){
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model){
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);
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

        //Nếu category không rỗng
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

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        String image = file!=null?file.getOriginalFilename():"default.jpg";
        product.setImage(image);

        //Set mặc điịnh discount
        product.setDiscount(0);
        //set giá discount mặc định là giá ban đầu vì discount 0%
        product.setDiscountPrice(product.getPrice());

        Product isSaved = productService.saveProduct(product);

        if(!ObjectUtils.isEmpty(isSaved)){
            File saveFile =  new ClassPathResource("static/img").getFile();
            //Tạo ra đường dẫn tới vị trí lưu hình ảnh, bao gồm tên file của hình ảnh.
            // separator đại diện cho dấu '/'
            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"product_images"+File.separator+file.getOriginalFilename());
            System.out.println(path);
            //Copy nội dung của file tải lên từ client và lưu nó vào đường dẫn path vừa tạo.
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            session.setAttribute("succMsg", "Product is successfully saved");
        }else {
            session.setAttribute("errorMsg", "something wrong to saved product");
        }

        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String viewProducts(Model model){
        List<Product> products = productService.getAllProduct();

        model.addAttribute("products",products);

        //trả về trang products.html trong thư mục admin
        return "/admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProductById(@PathVariable int id, HttpSession session){

        Boolean isDeleted = productService.deleteProduct(id);
        if (isDeleted){
            session.setAttribute("succMsg", "Delete product success");
        }else {
            session.setAttribute("errorMsg", "Delete product failed");
        }

        return "redirect:/admin/products";

    }

    @GetMapping("/editProduct/{id}")
    public String loadEidtProduct(@PathVariable int id, Model model){
        Product editProduct = productService.findProductById(id);
        List<Category> categories = categoryService.getAllCategory();

        model.addAttribute("product",editProduct);
        model.addAttribute("categories",categories);

        return "/admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile file, HttpSession session){

        //validate discount >= 0 && <=100
        if(product.getDiscount()<0 || product.getDiscount()>100){
            session.setAttribute("errorMsg", "Invalid discount");
        }else{
            Product updateProduct = productService.updateProduct(product, file);
            if(!ObjectUtils.isEmpty(updateProduct)){
                session.setAttribute("succMsg", "Update product success");
            }else {
                session.setAttribute("errorMsg", "Update product failed");
            }
        }

        return "redirect:/admin/editProduct/"+product.getId();

    }

    @GetMapping("/users")
    public String getAllUsers(Model model){
        List<UserDtls> users =  userService.getAllUsers("ROLE_USER");
        model.addAttribute("users",users);
        return "/admin/users";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,HttpSession session){
       Boolean f = userService.updateAccountStatus(id,status);
       if(f==true){
           session.setAttribute("succMsg", "Account Status Updated");
       }else {
           session.setAttribute("errorMsg", "Account Status failed");
       }


        return "redirect:/admin/users";
    }

    @GetMapping("/search-order")
    public String searchProduct(@RequestParam String orderId, Model model){

        return "/admin/orders";
    }

}
