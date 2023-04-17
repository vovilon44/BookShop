package com.example.MyBookShopApp.data.struct.book.file;

import com.example.MyBookShopApp.data.Book;

import javax.persistence.*;

@Entity
@Table(name = "book_file")
public class BookFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String hash;

    @Column(name = "type_id")
    private Integer typeId;

    private String path;

    @Transient
    private Long size;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    public String getBookFileExtensionString(){
        return BookFileType.getExtensionStringByTypeID(typeId);
    }

    public String getBookFileTypeDescription(){
        return BookFileType.getFileTypeDescription(typeId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getSize() {
        if (size > 1024 * 1024){
            return ((Math.round((double) size / (1024 * 1024)) * 100 ) / 100) + " MB";
        } else if (size > 1024) {
            return ((Math.round((double) size / 1024) * 100 ) / 100) + " KB";
        } else {
            return size + " Byte";
        }
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
