var revealObjects
async function reveal(){
    revealObjects = document.getElementsByClassName("is-revealing")
    for (const reveal of revealObjects){
       boundings =  reveal.parentElement.getBoundingClientRect(); 
       
       if (boundings.height - boundings.top < -200  )
        reveal.classList.remove("visible")
        reveal.classList.add("invisible")

       if ((boundings.top - window.innerHeight) < (0-(boundings.height) * 0.35)){
            reveal.classList.add("visible")
            reveal.classList.remove("invisible")
       }
    }
}
window.onscroll = function (e) {reveal()}
