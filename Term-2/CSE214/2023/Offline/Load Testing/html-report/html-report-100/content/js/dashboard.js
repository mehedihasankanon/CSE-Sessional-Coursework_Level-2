/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 77.28571428571429, "KoPercent": 22.714285714285715};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.7721428571428571, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.99, 500, 1500, "GET api/download/256"], "isController": false}, {"data": [0.995, 500, 1500, "POST /login-1"], "isController": false}, {"data": [1.0, 500, 1500, "POST /login-0"], "isController": false}, {"data": [0.19, 500, 1500, "GET /courses"], "isController": false}, {"data": [0.28, 500, 1500, "GET /notices"], "isController": false}, {"data": [1.0, 500, 1500, "GET /"], "isController": false}, {"data": [0.95, 500, 1500, "POST /login"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 700, 159, 22.714285714285715, 122.77571428571424, 14, 751, 80.5, 264.0, 323.5499999999994, 400.9100000000001, 7.011077502453877, 612.0309150958515, 1.9562158209971756], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["GET api/download/256", 100, 1, 1.0, 44.38000000000002, 25, 573, 32.0, 63.70000000000002, 79.0, 568.2299999999975, 1.00958092295888, 258.7277876422247, 0.2800009591018768], "isController": false}, {"data": ["POST /login-1", 100, 0, 0.0, 77.82000000000005, 33, 667, 67.0, 99.80000000000001, 113.84999999999997, 662.8799999999978, 1.00981540574383, 151.71462711303872, 0.27316295643656335], "isController": false}, {"data": ["POST /login-0", 100, 0, 0.0, 62.32999999999998, 27, 136, 59.5, 86.0, 90.84999999999997, 135.7999999999999, 1.009448437373819, 0.500781060728418, 0.24250421444722606], "isController": false}, {"data": ["GET /courses", 100, 81, 81.0, 233.45000000000005, 168, 298, 236.0, 274.8, 280.79999999999995, 297.98, 1.0082576299896149, 43.842468252487876, 0.27077231274135166], "isController": false}, {"data": ["GET /notices", 100, 72, 72.0, 262.4100000000001, 109, 412, 261.0, 367.0, 391.95, 411.98, 1.0087051252307413, 7.523915768078518, 0.2708924896859901], "isController": false}, {"data": ["GET /", 100, 0, 0.0, 38.61, 14, 92, 35.0, 60.80000000000001, 69.94999999999999, 91.89999999999995, 1.0098256031183415, 2.4003081230371515, 0.11833893786543064], "isController": false}, {"data": ["POST /login", 100, 5, 5.0, 140.43, 74, 751, 129.5, 178.70000000000002, 201.84999999999997, 746.8799999999978, 1.008959560900799, 152.08658354941883, 0.5153182132335137], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["The operation lasted too long: It took 410 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 236 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 1.8867924528301887, 0.42857142857142855], "isController": false}, {"data": ["The operation lasted too long: It took 268 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 370 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 214 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 251 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 246 milliseconds, but should not have lasted longer than 200 milliseconds.", 5, 3.1446540880503147, 0.7142857142857143], "isController": false}, {"data": ["The operation lasted too long: It took 234 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 231 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 392 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 273 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 276 milliseconds, but should not have lasted longer than 200 milliseconds.", 5, 3.1446540880503147, 0.7142857142857143], "isController": false}, {"data": ["The operation lasted too long: It took 241 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 224 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 244 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 298 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 202 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 1.8867924528301887, 0.42857142857142855], "isController": false}, {"data": ["The operation lasted too long: It took 311 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 350 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 266 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 243 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 1.8867924528301887, 0.42857142857142855], "isController": false}, {"data": ["The operation lasted too long: It took 222 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 330 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 379 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 239 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 253 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 232 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 1.8867924528301887, 0.42857142857142855], "isController": false}, {"data": ["The operation lasted too long: It took 218 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 238 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 259 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 1.8867924528301887, 0.42857142857142855], "isController": false}, {"data": ["The operation lasted too long: It took 290 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 212 milliseconds, but should not have lasted longer than 200 milliseconds.", 4, 2.5157232704402515, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 315 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 296 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 336 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 228 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 408 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 249 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 248 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 219 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 252 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 412 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 262 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 297 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 203 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 277 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 229 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 281 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 364 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 271 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 345 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 258 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 573 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 242 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 1.8867924528301887, 0.42857142857142855], "isController": false}, {"data": ["The operation lasted too long: It took 282 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 267 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 329 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 257 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 302 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 751 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 391 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 339 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 230 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 235 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 272 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 225 milliseconds, but should not have lasted longer than 200 milliseconds.", 4, 2.5157232704402515, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 356 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 401 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 359 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 349 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 227 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 366 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 346 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 270 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 208 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 247 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 205 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 324 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 292 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 237 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 260 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 327 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 347 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 264 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 1.8867924528301887, 0.42857142857142855], "isController": false}, {"data": ["The operation lasted too long: It took 207 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 211 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 217 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 357 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 275 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 254 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 201 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 216 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 371 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 206 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 220 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 367 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 342 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 328 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 294 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 1.2578616352201257, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 284 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}, {"data": ["The operation lasted too long: It took 332 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 0.6289308176100629, 0.14285714285714285], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 700, 159, "The operation lasted too long: It took 246 milliseconds, but should not have lasted longer than 200 milliseconds.", 5, "The operation lasted too long: It took 276 milliseconds, but should not have lasted longer than 200 milliseconds.", 5, "The operation lasted too long: It took 212 milliseconds, but should not have lasted longer than 200 milliseconds.", 4, "The operation lasted too long: It took 225 milliseconds, but should not have lasted longer than 200 milliseconds.", 4, "The operation lasted too long: It took 236 milliseconds, but should not have lasted longer than 200 milliseconds.", 3], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["GET api/download/256", 100, 1, "The operation lasted too long: It took 573 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["GET /courses", 100, 81, "The operation lasted too long: It took 246 milliseconds, but should not have lasted longer than 200 milliseconds.", 4, "The operation lasted too long: It took 225 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, "The operation lasted too long: It took 276 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, "The operation lasted too long: It took 243 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, "The operation lasted too long: It took 259 milliseconds, but should not have lasted longer than 200 milliseconds.", 3], "isController": false}, {"data": ["GET /notices", 100, 72, "The operation lasted too long: It took 212 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, "The operation lasted too long: It took 276 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 311 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 264 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 216 milliseconds, but should not have lasted longer than 200 milliseconds.", 2], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /login", 100, 5, "The operation lasted too long: It took 222 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 339 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 202 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 207 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 751 milliseconds, but should not have lasted longer than 200 milliseconds.", 1], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
