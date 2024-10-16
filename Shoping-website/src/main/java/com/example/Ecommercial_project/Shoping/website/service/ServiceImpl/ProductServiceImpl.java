package com.example.Ecommercial_project.Shoping.website.service.ServiceImpl;

import com.example.Ecommercial_project.Shoping.website.model.Product;
import com.example.Ecommercial_project.Shoping.website.repository.ProductRepository;
import com.example.Ecommercial_project.Shoping.website.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }


    @Override
    public Boolean deleteProduct(Integer id) {
        Product productDelete = productRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(productDelete)){
            productRepository.delete(productDelete);
            return true;
        }

        return false;

    }

    @Override
    public Product findProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product updateProduct(Product product, MultipartFile file) {

        String productImage = file!=null?file.getOriginalFilename():product.getImage();

        Product oldProduct = findProductById(product.getId());



        oldProduct.setTitle(product.getTitle());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setImage(productImage);
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStock(product.getStock());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setIsActive(product.getIsActive());
        oldProduct.setDiscount(product.getDiscount());

        //tiền giảm 5 = 100*(5/100); => giá discount = giá - tiền giảm
        //tiền giảm
        Double discount = product.getPrice()*(product.getDiscount()/100.0);
        //giá sau khi giảm
        Double discountPrice = product.getPrice()-discount;
        oldProduct.setDiscountPrice(discountPrice);

        Product updateProduct = productRepository.save(oldProduct);


        if(!ObjectUtils.isEmpty(updateProduct)){
            if(!file.isEmpty()){
                try {
                    File saveFile =  new ClassPathResource("static/img").getFile();
                    //Tạo ra đường dẫn tới vị trí lưu hình ảnh, bao gồm tên file của hình ảnh.
                    // separator đại diện cho dấu '/'
                    Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"product_images"+File.separator+file.getOriginalFilename());
                    System.out.println(path);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    //Copy nội dung của file tải lên từ client và lưu nó vào đường dẫn path vừa tạ
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            return product;
        }

        return null;
    }

    @Override
    public List<Product> getAllActiveProduct(String category) {
        List<Product> products= null;
        //Nếu không chọn category thì lấy ra tất cả product active
        if (ObjectUtils.isEmpty(category)){
            products= productRepository.findByIsActiveTrue();
        }else {
            //Có category thì lấy theo category
            products = productRepository.findByCategory(category);
        }


        return products;
    }
}
