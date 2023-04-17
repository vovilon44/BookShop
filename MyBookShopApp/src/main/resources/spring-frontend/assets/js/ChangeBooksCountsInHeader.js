async function ChangeBooksCountsInHeader (){
    let response = await fetch("/api/getBooksCountMainKeptCart");
    let result = await response.json();
    if (result.result){
        document.querySelector('.CartBlock-amount.cart').textContent = result.cart
        document.querySelector('.CartBlock-amount.kept').textContent = result.kept
        document.querySelector('.CartBlock-amount.main').textContent = result.main
        document.querySelector('.CartBlock-text').textContent = result.name
        document.querySelector('.CartBlock-budget').textContent = 'Счет ' + Math.round(result.balance * 100) / 100 + " руб"
    } else {
        for (let str of document.cookie.split(";")){
            console.log("str: ",str)
            if (str.indexOf("keptContents") >= 0){
                let slugsKept = str.split("=")[1].split("/")
                if (slugsKept[0] === ""){
                    document.querySelector('.CartBlock-amount.kept').textContent = 0;
                } else {
                    document.querySelector('.CartBlock-amount.kept').textContent = slugsKept.length;
                }
            } else if (str.indexOf("cartContents") >= 0){
                let slugsCart = str.split("=")[1].split("/")
                if (slugsCart[0] === ""){
                    document.querySelector('.CartBlock-amount.cart').textContent = 0;
                } else {
                    document.querySelector('.CartBlock-amount.cart').textContent = slugsCart.length;
                }
            }
        }
    }
}

function showTags (e){
    let tags = document.getElementById('tags')
    if (tags.hasAttribute('hidden')){
        tags.removeAttribute("hidden")
    } else {
        tags.setAttribute('hidden', 'hidden')
    }

}

function changeIndexForLinkCards(){
    document.querySelectorAll('.Card-ribbon').forEach(e=>e.addEventListener('mouseover', mouseOverCard, true))
    document.querySelectorAll('.Card-ribbon').forEach(e=>e.addEventListener('mouseout', mouseOutFromCard, true))
    document.querySelectorAll('strong a').forEach(e=>e.addEventListener('mouseout', mouseOutFromLink))
}


function mouseOverCard(){
    let link = this.closest('div.Card').querySelector('strong a')
    if (link.style.position !== "relative") {
        link.style.zIndex = "12122";
        link.style.position = "relative"
    }
}

function mouseOutFromCard(e){
    let link = this.closest('div.Card').querySelector('strong a')
    if (e.relatedTarget !== link){
        link.style.zIndex = null;
        link.style.position = null;
    }

}

function mouseOutFromLink(e){
    let link = e.target
    link.style.zIndex = null;
    link.style.position = null;
}

function ff(){
        if (document.location.search.indexOf('result') > 0){
            let elems = document.querySelectorAll('.Tabs-link')
            elems[0].classList.remove('Tabs-link_ACTIVE')
            elems[2].classList.add('Tabs-link_ACTIVE')
        }
        console.log("custom")
}

