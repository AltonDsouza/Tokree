package com.mkretail.retail.Utils;

import android.widget.BaseAdapter;

public interface AppConstant {

    String APP_SERVER_URL = "http://fortunehealthplus.com/Tokree/Topi/";

    String login=APP_SERVER_URL+"Login.php";

    String GetUser=APP_SERVER_URL+"GetUser.php";
    String UpdateName=APP_SERVER_URL+"UpdateName.php";
    String ChangePwdOtp=APP_SERVER_URL+"ChangePwdOtp.php";
    String ChangePassword=APP_SERVER_URL+"ChangePassword.php";

    //////////////////////////////////////////////////////////////////////

    String WebURL = "http://tokree.co.in/";
    String BaseURl="http://tokree.co.in/Topi/";

   // "tokree.co.in/Topi";
//    String AssetsURL="http://fortunehealthplus.com/Tokree/";

    String HomeC="Home/";
    String TokreeCat=BaseURl+HomeC+"GetCat";
    String TokreeSubCat=BaseURl+HomeC+"GetSubCat";
    String TokreeProdList=BaseURl+HomeC+"ProductList";
    String TokreeProductItems = BaseURl+HomeC+"ProductItems";
    String AddToCart = "Cart/AddToCart";
    String UpdateCart = BaseURl+"Cart/UpdateCartProduct";
    String DeleteCart = BaseURl+"Cart/DeleteCartProduct";
    String TokreeSearch = BaseURl+HomeC+"GetAllProduct";
    String Search = BaseURl+HomeC+"Search";
    String CartCount = BaseURl+"Cart/CartCount";
    String AddToWishList = BaseURl+"WishList/AddToWishList";
    String DelWishList = BaseURl+"WishList/DeleteWLProduct";
    String GetWishList = BaseURl+"WishList/GetWishList";
    String ReplaceOrder = BaseURl+"User/GetOrderDetail";
    String ComplaintTitle = BaseURl + "ComplaintTopi/ComplaintTitlesTopi";
    String InsertComplaint = BaseURl + "ComplaintTopi/InsertComplaintTopi";
    String SubmitFeedback = BaseURl+"ComplaintTopi/InsertFeedback";
    String SubmitReplacement = BaseURl+"Replace/SendReq";

//    String Register="Register/";
    String TokreeReg=BaseURl+"Register/isUserExists";
    String TokreeRefer = BaseURl+"Register/isReferal";
    String TokreeUserRegister=BaseURl+"Register/UserRegister";

    String Auth="Auth/";
    String ReferCode = BaseURl+HomeC+"GetReferCode";
    String TokreeLogin=BaseURl+Auth+"Login";

    String Area = BaseURl+"Home/GetArea";
    String GetNotifications = BaseURl+"User/GetNotifications";


    String TokreeChangePwd=BaseURl+Auth+"ChangePassword";
    String PopUp = BaseURl+"Additionals/GetPopUP";


}
