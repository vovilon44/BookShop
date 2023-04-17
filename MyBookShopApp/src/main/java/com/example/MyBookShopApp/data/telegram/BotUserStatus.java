package com.example.MyBookShopApp.data.telegram;


public enum BotUserStatus {

    LOGIN, SEARCH_GENRE,SEARCH_AUTHOR,SEARCH_TITLE,SEARCH_TAG,SEARCH_ALL,KEPT, CART, MY, ARCHIVE;

        public static BotUserStatus of (int value) {
            switch (value) {
                case 0:
                    return BotUserStatus.LOGIN;
                case 1:
                    return BotUserStatus.SEARCH_GENRE;
                case 2:
                    return BotUserStatus.SEARCH_AUTHOR;
                case 3:
                    return BotUserStatus.SEARCH_TITLE;
                case 4:
                    return BotUserStatus.SEARCH_TAG;
                case 5:
                    return BotUserStatus.SEARCH_ALL;
                case 6:
                    return BotUserStatus.KEPT;
                case 7:
                    return BotUserStatus.CART;
                case 8:
                    return BotUserStatus.MY;
                case 9:
                    return BotUserStatus.ARCHIVE;
                default:
                    return null;
            }
        }


    @Override
    public String toString() {
        switch (this) {
            case LOGIN:
                return "аутентификация";
            case SEARCH_GENRE:
                return "поиск по жанру";
            case SEARCH_AUTHOR:
                return "поиск по автору";
            case SEARCH_TITLE:
                return "поиск по названию";
            case SEARCH_TAG:
                return "поиск по тэгу";
            case SEARCH_ALL:
                return "общий поиск";
            case KEPT:
                return "отложенные";
            case CART:
                return "корзина";
            case MY:
                return "мои книги";
            case ARCHIVE:
                return "архив";
            default:
                return "null";
        }
    }

    public int getValue(){
        switch (this) {
            case LOGIN:
                return 0;
            case SEARCH_GENRE:
                return 1;
            case SEARCH_AUTHOR:
                return 2;
            case SEARCH_TITLE:
                return 3;
            case SEARCH_TAG:
                return 4;
            case SEARCH_ALL:
                return 5;
            case KEPT:
                return 6;
            case CART:
                return 7;
            case MY:
                return 8;
            case ARCHIVE:
                return 9;
            default:
               return  -1;
        }
    }

}

//
//package com.example.MyBookShopApp.data.telegramm;
//
//
//public enum BotUserStatus {
//
//    LOGIN(1), SEARCH(2),SEARCH_GENRE(3),SEARCH_AUTHOR(4),SEARCH_TITLE(5),SEARCH_TAG(6),SEARCH_ALL(7), CART(8), PAY(9);
//
//    private final Integer value;
//
//
//    BotUserStatus(int value) {
//        this.value = value;
//    }
//
//    public Integer getValue() {
//        return value;
//    }
//
//    public static BotUserStatus of (int value){
//        switch (value){
//            case 1: return BotUserStatus.LOGIN;
//            case 2: return BotUserStatus.SEARCH;
//            case 3: return BotUserStatus.SEARCH_GENRE;
//            case 4: return BotUserStatus.SEARCH_AUTHOR;
//            case 5: return BotUserStatus.SEARCH_TITLE;
//            case 6: return BotUserStatus.SEARCH_TAG;
//            case 7: return BotUserStatus.SEARCH_ALL;
//            case 8: return BotUserStatus.CART;
//            case 9: return  BotUserStatus.PAY;
//            default: return null;
//        }
//    }
//

//    }
//}
