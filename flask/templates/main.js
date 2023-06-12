function show(){
    const api = {
        key: "17958374ca16d208ce5014aac01e8031",
        base: "https://api.openweathermap.org/data/2.5/"
    }

    StartApp();
    getResults('Melbourne, AU');
    event.preventDefault();
    // alert(sb.value);

function getResults(query){
    fetch(`${api.base}weather?q=${query}&units=metric&APPID=${api.key}`)
    .then(weather => {
        return weather.json();
    }).then(displayResults);
}

function StartApp(){
    getResults('Melbourne, AU');
    // alert(sb.value);
}


function displayResults(weather){
    console.log(weather);
    let city = document.querySelector('.location .city');
    city.innerText = `${weather.name}, ${weather.sys.country}`;

    let now = new Date();
    let date = document.querySelector('.location .date');
    date.innerText = showdate(now);

    
    let tempurature = document.querySelector('.current .temp');
    tempurature.innerHTML = `${Math.round(weather.main.temp)}<span>°C</span>`;

    let weather_el = document.querySelector('.current .weather');
    weather_el.innerText = `Status: ${weather.weather[0].main}`;

    let humid = document.querySelector('.current .humidity');
    humid.innerText = `Humid: ${weather.main.humidity}%`;
}

function showdate(d){
    let months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    let days = ["Sunday,", "Monday,", "Tuesday,", "Wednesday,", "Thursday,", "Friday,", "Saturnday,"];

    let day = days[d.getDay()];
    let date = d.getDate();
    let month = months[d.getMonth()];
    let year = d.getFullYear();
    return `${day} ${date} ${month} ${year}`;
}
};

function init () {
	show();
 }
window.onload = init;