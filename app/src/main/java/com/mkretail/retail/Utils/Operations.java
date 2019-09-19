package com.mkretail.retail.Utils;

public interface Operations {

    void addToCart(String p_imid, String count);

    void updateCart(String p_imid, String count);

    void deleteCart(String p_imid, String count);
}
