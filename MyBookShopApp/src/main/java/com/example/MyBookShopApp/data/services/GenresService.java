package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.GenreDto;
import com.example.MyBookShopApp.data.repositories.GenreRepository;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GenresService {


    private final GenreRepository genreRepository;

    @Autowired
    public GenresService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }


    //--------------------------------------------yea
    public GenreEntity getGenreEntityFromSlug(String slug){
        return genreRepository.findGenreEntityBySlug(slug);
    }
    //--------------------------------------------yea
    public ArrayList<GenreEntity> getGenreTree(String genreSlug){
        GenreEntity genre = getGenreEntityFromSlug(genreSlug);
        ArrayList<GenreEntity> genreTree;
        if (genre.getParentId() != null){
            genreTree = getGenreTree(new ArrayList<>(), genre.getParentId());
            Collections.reverse(genreTree);
        } else {
            return new ArrayList<>();
        }
        return genreTree;
    }

    private ArrayList<GenreEntity> getGenreTree(ArrayList<GenreEntity> list, Integer id){
        GenreEntity genre = getGenreEntityFromId(id);
        list.add(genre);
        if (genre.getParentId() != null) {
            getGenreTree(list, genre.getParentId());
        } else {
            return list;
        }
        return list;
    }

    public GenreEntity getGenreEntityFromId(Integer id){
        return genreRepository.findGenreEntityById(id);
    }



    public String getElementsHtml(String locale) {
        List<GenreEntity> genres = genreRepository.findAllByOrderByParentIdAscIdDesc();
        List<GenreDto> genreDtoList = new ArrayList<>();
        while (genres.get(genres.size() - 1).getParentId() == null) {
            genreDtoList.add(new GenreDto(genres.get(genres.size() - 1).getId(), changeFromTagToTags(getTagElement(genres.get(genres.size() - 1), locale)) ));
            genres.remove(genres.size() - 1);
        }
        for (GenreEntity genre : genres) {
            for (GenreDto genreDto : genreDtoList){
                if (genreDto.getIds().contains(genre.getParentId())){
                    genreDto.setElement(addGenreElement(genreDto, genre.getParentId(), getTagElement(genre, locale)));
                    genreDto.addId(genre.getId());
                }
            }
        }
        for (GenreDto genreDto : genreDtoList){
            for (Element element : genreDto.getElement().getElementsByAttribute("id")){
                element.removeAttr("id");
            }
        }
        StringBuilder sb = new StringBuilder();
        for (GenreDto genreDto : genreDtoList) {
            sb.append(genreDto.getElement());
        }

        return sb.toString();
    }

    private Element addGenreElement(GenreDto genreDto, int id, Element newElement) {
        Element elementDto = genreDto.getElement();
        Element elementForChange = elementDto.getElementById(String.valueOf(id));
        if (elementForChange.parent().hasClass("Tags-title")){
            elementForChange.parents().get(1).appendChild(newElement);
        } else {
            Element parent = elementForChange.parent();
            parent.appendChild(changeFromTagToTags(elementForChange).appendChild(newElement));
            Elements parents = elementForChange.parents();
            if (parents.size() >= 3) {
                parents.get(2).addClass("Tags_embed");
            }
        }
        return elementDto;
    }

    private Element changeFromTagToTags(Element element){
        return new Element("div").addClass("Tags").appendChild(new Element("div").addClass("Tags-title").appendChild(element));

    }

    private Element getTagElement(GenreEntity genre, String locale){
        return new Element("div")
                .addClass("Tag").id(String.valueOf(genre.getId()))
                .appendChild(new Element("a")
                        .attr("href", "/genres/" + genre.getSlug())
                        .text(locale.equals("ru") ? genre.getRuName() : genre.getEnName())
                        .appendChild(new Element("span")
                                .addClass("undefined-amount").text("("+ genre.getBookCount() +")")));
    }

}
