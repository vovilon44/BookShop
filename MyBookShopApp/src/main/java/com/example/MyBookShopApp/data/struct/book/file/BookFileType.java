package com.example.MyBookShopApp.data.struct.book.file;

public enum BookFileType
{
    PDF("pdf"), EPUB("epub"), FB2("fb2"), TXT("txt");

    private final String fileExtensionString;

    BookFileType(String fileExtensionString){
        this.fileExtensionString = fileExtensionString;
    }

    public static String getExtensionStringByTypeID(Integer typeId){
        switch (typeId){
            case 1: return BookFileType.PDF.fileExtensionString;
            case 2: return BookFileType.EPUB.fileExtensionString;
            case 3: return BookFileType.FB2.fileExtensionString;
            case 4: return BookFileType.TXT.fileExtensionString;
            default: return "";
        }
    }

    public static BookFileType getFileTypeByTypeID(Integer typeId){
        switch (typeId){
            case 1: return BookFileType.PDF;
            case 2: return BookFileType.EPUB;
            case 3: return BookFileType.FB2;
            case 4: return BookFileType.TXT;
            default: return null;
        }
    }

    public static String getFileTypeDescription(Integer typeId){
        switch (typeId){
            case 1: return "Просто pdf";
            case 2: return "Просто epub";
            case 3: return "Просто fb2";
            case 4: return "Просто txt";
            default: return "";
        }
    }

    public Integer getNumber(){
        switch (this){
            case PDF: return 3;
            case EPUB: return 1;
            case FB2: return 2;
            case TXT: return 4;
            default: return null;
        }
    }
}
