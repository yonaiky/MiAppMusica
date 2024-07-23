class LoadingScreen {
  static loaded() {
    const loadingScreen = document.querySelector(".loadingScreen");
    loadingScreen.style.opacity = "0";
    setTimeout(() => {
      loadingScreen.style.display = "none";
    }, 400)
  }
}
// Dark Mode
if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {       
  document.body.classList.add("dark");
}
function toggleDark() {
  document.body.classList.toggle("dark");
}
function detecttouch(){
  document.getElementById("toggledark").classList.remove("focustoggle")
}
