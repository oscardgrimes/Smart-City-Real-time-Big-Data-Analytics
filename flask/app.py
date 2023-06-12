
from flask import Flask, render_template
import os
import urllib2

#connect to Cassandra
app = Flask(__name__)
from cassandra.cluster import Cluster
cluster = Cluster(['127.0.0.1'])
session = cluster.connect('test')

TEMPLATE_DIR = os.path.abspath('templates')
STATIC_DIR = os.path.abspath('static')
app = Flask(__name__, template_folder=TEMPLATE_DIR, static_folder=STATIC_DIR)

#route for status page
@app.route('/index.html')
def index_app2():
  return render_template('index.html')

@app.route('/')
def index_app():
  return render_template('index.html')

#route for javascipt
@app.route('/main.js')
def Main_js():
  return render_template('main.js')

#rout for prediction
@app.route('/prediction.html')
def prediction_test():
  html = '''<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name ="website" content="frontend">
        <meta http-equiv="X-UA-Compatible" content="ie-edge">
        <title>Weather Website</title>
        <link rel= "stylesheet" type= "text/css" href= "/static/main.css">
    </head>
    <body>
        <div class="app-wrap">
            <nav>
                <label class="logo">SEPA6</label>
                <ul>
                    <li><a href="index.html">Status</a></li>
                    <li><a href="prediction.html">Fog And HAze</a></li>
                </ul>
            </nav>
           <header>
               <h1>FOG AND HAZE PREDICTION</h1>
            </header>
            <main>
 
                <section class="location">
                    <div class="city">Melbourne, AU</div>
                    <div class="date">Saturnday 29 August 2021</div>
                     <div class="clock">
                    <span class="clocktime"></span>
                    <span class="clockamorpm"></span>
                  </div>          
                  <script>
                      class Clock {
                            constructor(element) {
                              this.element = element;
                            }

                            start() {
                              this.update();

                              setInterval(() => {
                                this.update();
                              }, 500);
                            }
                            
                            //update time per second//
                            update() {
                              const parts = this.getTime();
                              const formatMin = parts.minute.toString().padStart(2, "0");
                              const timeFormatted = `${parts.hour}:${formatMin}`;
                              const amPm = parts.Am ? "AM" : "PM";

                              this.element.querySelector(".clocktime").textContent = timeFormatted;
                              this.element.querySelector(".clockamorpm").textContent = amPm;
                            }


                            //get real time//
                            getTime() {
                              const now = new Date();
                              var utc_offset = now.getTimezoneOffset();
                              now.setMinutes(now.getMinutes()+ utc_offset);
                            
                            //get Melboure time//
                              var melbourne = 11*60;
                              now.setMinutes(now.getMinutes() +melbourne);
                              return {
                                hour: now.getHours() % 12 || 12,
                                minute: now.getMinutes(),
                                Am: now.getHours() < 12
                              };
                            }
                          }
                          const clockElement = document.querySelector(".clock");
                          const clockObject = new Clock(clockElement);
                          clockObject.start();

                  </script>
                </section>
               <div class= "prediction">
                <table id="table1">
                    <theadh>
                    <tr>
                      <th id="tabletitle">FOG PREDICTION</th>
                      <th id="tabletitle">HAZE PREDICTION</th>
                    </tr>
                </theadh>
                <tbody>
                    <tr>
                      <td id="mainprd">fogvalue1</td>
                      <td id="mainprd">hazevalue1</td>
                    </tr>
                </tbody>
                  </table>

                <table id="table2">
                    <theadh>
                        <tr>
                        <th id="tabletitle2">TIME</th>
                          <th id="tabletitle2">FOG PREDICTION</th>
                          <th id="tabletitle2">HAZE PREDICTION</th>
                        </tr>
                    </theadh>
                    <tbody>
                        <tr>
                          <td>time2</td>
                          <td>fogvalue2</td>
                          <td>hazevalue2</td>
                        </tr>
                        <tr>
                            <td>time3</td>
                            <td>fogvalue3</td>
                            <td>hazevalue3</td>
                        </tr>
                        <tr>
                            <td>time4</td>
                            <td>fogvalue4</td>
                            <td>hazevalue4</td>
                        </tr>
                        <tr>
                            <td>time5</td>
                            <td>fogvalue5</td>
                            <td>hazevalue5</td>
                        </tr>
                        <tr>
                            <td>time6</td>
                            <td>fogvalue6</td>
                            <td>hazevalue6</td>
                        </tr>
                        <tr>
                            <td>time7</td>
                            <td>fogvalue7</td>
                            <td>hazevalue7</td>
                        </tr>
                    </tbody>
                </table>
               </div>
           </main>
        </div>
        <script src="main.js"></script>
    </body>
</html>'''

  data = session.execute('SELECT toTimestamp(timeuuid) as timestamp, haze_prediction, fog_prediction FROM predictions WHERE city_id = 1 ORDER by timeuuid DESC limit 10;')
  count = 0
  for each in data:                          #print No with 0 and Yes with 1
      count = count + 1
      if(each.fog_prediction == 0.0):
        html = html.replace('fogvalue'+str(count), str('NO'))
      if(each.fog_prediction == 1.0):
        html = html.replace('fogvalue'+str(count), 'YES')
      if(each.fog_prediction == 0.0):
        html = html.replace('hazevalue'+str(count), 'NO')
      if(each.fog_prediction == 1.0):
        html = html.replace('hazevalue'+str(count), 'YES')
      if (count > 1):                                                             #select only time from cassandra table
        hrs = str(int(str(each.timestamp).split(' ')[1][0:2]) + 11)                    
        mins = str(each.timestamp).split(' ')[1][3:5]
        time = hrs + ':' + mins
        html = html.replace('time'+str(count), time)
  
  return html


if __name__ == '__main__':
  app.run('110.173.132.3')
  
