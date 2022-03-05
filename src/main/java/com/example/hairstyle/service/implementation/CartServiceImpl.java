package com.example.hairstyle.service.implementation;

import com.example.hairstyle.constant.ResponseText;
import com.example.hairstyle.entity.CartItem;
import com.example.hairstyle.payload.request.CartProductRequest;
import com.example.hairstyle.payload.response.MessageResponse;
import com.example.hairstyle.repository.CartItemRepository;
import com.example.hairstyle.repository.CartRepository;
import com.example.hairstyle.repository.ProductRepository;
import com.example.hairstyle.repository.ProfileRepository;
import com.example.hairstyle.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ProfileRepository profileRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ResponseEntity addItem(CartProductRequest cartProductRequest, String username) {
        var profileOptional = profileRepository.findByAccount_Username(username);

        if (profileOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PROFILE));
        }

        var productOptional = productRepository.findById(cartProductRequest.getProductId());

        if (productOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PRODUCT));
        }

        var product = productOptional.get();
        var profile = profileOptional.get();
        var cart = profile.getCart();
        var cartItems = cart.getCartItems();
        var cartItemOptional = cartItems
                .stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();
        CartItem cartItem;
        if (cartItemOptional.isEmpty()) {
             cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setDiscount(product.getDiscount());
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setQuantity(cartProductRequest.getQuantity());

            var totalPrice = product.getPrice() * cartProductRequest.getQuantity() * product.getDiscount() / 100;
            cartItem.setTotalPrice(totalPrice);

            product.addCartItem(cartItem);
            cart.addCartItem(cartItem);
        } else {
            cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartProductRequest.getQuantity());
            var totalPrice = product.getPrice() * cartProductRequest.getQuantity() * product.getDiscount() / 100;
            cartItem.setTotalPrice(totalPrice);
        }
        if(cartItem.getQuantity()>cartItem.getProduct().getQuantity()){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.INVALID_QUANTITY));
        }

        cartItemRepository.save(cartItem);
        return ResponseEntity
                .ok(cart);
    }

    @Override
    public ResponseEntity removeItem(Long productId, String username) {
        var profileOptional = profileRepository.findByAccount_Username(username);

        if (profileOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PROFILE));
        }

        var productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PRODUCT));
        }

        var product = productOptional.get();
        var profile = profileOptional.get();
        var cart = profile.getCart();
        var cartItems = cart.getCartItems();
        var cartItemOptional = cartItems
                .stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();

        if(cartItemOptional.isEmpty()){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PRODUCT_IN_CART));
        }

        var cartItem = cartItemOptional.get();

        cart.removeCartItem(cartItem);
        product.removeCartItem(cartItem);

        cartItemRepository.delete(cartItem);
        return ResponseEntity
                .ok(new MessageResponse(true, ResponseText.DELETE_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity getAll(String username) {
        var userOptional = profileRepository.findByAccount_Username(username);
        if(userOptional.isEmpty()){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PROFILE));
        }

        var user = userOptional.get();
        var cartItems = user.getCart().getCartItems();
        return ResponseEntity
                .ok(cartItems);
    }
}
