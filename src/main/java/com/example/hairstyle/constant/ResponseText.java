package com.example.hairstyle.constant;

public class ResponseText {
    //Auth error
    public static final String INVALID_VERIFICATION_ERROR = "Verification code is invalid";
    public static final String PASSWORD_MISMATCH_ERROR = "Password is mismatched";
    public static final String EXISTED_ACCOUNT_ERROR = "Username is already taken";
    public static final String EXISTED_EMAIL_ERROR = "Email is already taken";
    public static final String NONEXISTENT_EMAIL_ERROR = "Email is not found";
    public static final String MISMATCHED_EMAIL_ERROR = "Email is mismatched";
    public static final String NONEXISTENT_ROLE_ERROR = "Role is not found";
    public static final String NONEXISTENT_ACCOUNT_ERROR = "Account is not found";
    public static final String NONEXISTENT_USER_ERROR = "User is not found";
    public static final String INCORRECT_PASSWORD_ERROR = "Password is incorrect";
    public static final String INCORRECT_USERNAME_PASSWORD_ERROR = "Username or password is incorrect";
    public static final String UNAUTHORIZED_ERROR = "Unauthorized error";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    public static final String DISABLED_ACCOUNT = "User is disabled";

    //Auth_noti
    public static final String CHANGE_PASSWORD_SUCCESSFULLY = "Password is changed successfully";
    public static final String VERIFY_EMAIL = "Verify account by email";
    public static final String VERIFY_PASSWORD_RECOVERY = "Verify password recovery by email";
    public static final String NO_DATA_RETRIEVAL = "No data retrieval";
    public static final String DISABLE_ACCOUNT_SUCCESSFULLY = "Disable account successfully";
    public static final String ENABLE_ACCOUNT_SUCCESSFULLY = "Enable account successfully";
    public static final String UPDATE_SUCCESSFULLY = "Update successfully";
    public static final String DELETE_SUCCESSFULLY = "Delete successfully";
    public static final String CREATE_SUCCESSFULLY = "Create successfully";

    //Hairstyle error
    public static final String UNFOUNDED_HAIRSTYLE = "Hairstyle is unfounded";
    public static final String UNFOUNDED_HAIR_LENGTH = "Hair length is unfounded";
    public static final String UNFOUNDED_HAIR_CATEGORY = "Hair category is unfounded";
    public static final String UNFOUNDED_FACE_SHAPE = "Face shape is unfounded";
    public static final String UNFOUNDED_AGE_RANGE = "Age range is unfounded";
    public static final String EXISTED_HAIRSTYLE = "Hairstyle is existed";

    //Profile error
    public static final String UNFOUNDED_PROFILE = "Profile is unfounded.";

    //Category error
    public static final String UNFOUNDED_CATEGORY = "Category is unfounded.";
    public static final String UNFOUNDED_CATEGORY_PARENT = "Category parent is unfounded.";

    //Discount error
    public static final String EXISTED_DISCOUNT = "Discount is existed.";


    //Product error
    public static final String UNFOUNDED_PRODUCT = "Product is unfounded.";
    public static final String EXISTED_PRODUCT = "Product is existed.";
    public static final String UNFOUNDED_PRODUCT_IN_CART = "Product is unfounded in cart.";

    //Product noti
    public static final String SUCCESSFUL_PAYMENT = "Transaction is successful.";

    public static final String INVALID_QUANTITY = "Quantity is invalid";
}
