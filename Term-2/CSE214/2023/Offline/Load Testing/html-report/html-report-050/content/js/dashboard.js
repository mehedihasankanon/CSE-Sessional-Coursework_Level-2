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

    var data = {"OkPercent": 75.71428571428571, "KoPercent": 24.285714285714285};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.7571428571428571, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.96, 500, 1500, "GET api/download/256"], "isController": false}, {"data": [1.0, 500, 1500, "POST /login-1"], "isController": false}, {"data": [1.0, 500, 1500, "POST /login-0"], "isController": false}, {"data": [0.16, 500, 1500, "GET /courses"], "isController": false}, {"data": [0.32, 500, 1500, "GET /notices"], "isController": false}, {"data": [1.0, 500, 1500, "GET /"], "isController": false}, {"data": [0.86, 500, 1500, "POST /login"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 350, 85, 24.285714285714285, 133.45428571428576, 19, 460, 95.0, 273.90000000000003, 340.0499999999999, 411.49, 3.5421516040886547, 309.21186731479605, 0.9883235502479506], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["GET api/download/256", 50, 2, 4.0, 56.82000000000001, 26, 457, 42.0, 86.39999999999999, 153.69999999999965, 457.0, 0.5090094675760969, 130.44510889621296, 0.14117059452305813], "isController": false}, {"data": ["POST /login-1", 50, 0, 0.0, 98.27999999999999, 41, 329, 81.5, 149.39999999999998, 249.84999999999985, 329.0, 0.5098919029165817, 76.6062889589282, 0.1379297432694269], "isController": false}, {"data": ["POST /login-0", 50, 0, 0.0, 64.04, 27, 94, 69.0, 84.69999999999999, 87.79999999999998, 94.0, 0.5100323360501056, 0.25302385421235707, 0.12252729948078708], "isController": false}, {"data": ["GET /courses", 50, 42, 84.0, 243.90000000000003, 178, 460, 241.0, 282.09999999999997, 336.24999999999966, 460.0, 0.5081559022308044, 22.096345565069363, 0.13646764952487422], "isController": false}, {"data": ["GET /notices", 50, 34, 68.0, 266.93999999999994, 114, 412, 272.0, 395.0, 409.34999999999997, 412.0, 0.508217883170873, 3.7907892496671174, 0.13648429479686533], "isController": false}, {"data": ["GET /", 50, 0, 0.0, 41.540000000000006, 19, 107, 35.0, 69.9, 75.35, 107.0, 0.5100687572684798, 1.2124095265541794, 0.05977368249239998], "isController": false}, {"data": ["POST /login", 50, 7, 14.0, 162.66, 90, 404, 147.5, 219.2, 332.3499999999999, 404.0, 0.509668409732628, 76.82555459886548, 0.2603091584864887], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["The operation lasted too long: It took 204 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 267 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 329 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 333 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 236 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 354 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 396 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 230 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 283 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 214 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 251 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 3.5294117647058822, 0.8571428571428571], "isController": false}, {"data": ["The operation lasted too long: It took 272 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 246 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 225 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 299 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 395 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 356 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 231 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 273 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 269 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 404 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 363 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 241 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, 3.5294117647058822, 0.8571428571428571], "isController": false}, {"data": ["The operation lasted too long: It took 227 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 270 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 298 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 205 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 411 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 221 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 202 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 266 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 237 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 260 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 263 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 222 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 285 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 383 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 264 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 238 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 286 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 290 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 212 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 233 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 320 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 315 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 460 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 336 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 389 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 408 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 201 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 249 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 216 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 457 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 252 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 412 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 262 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 297 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 328 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 265 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 203 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 229 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 322 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 361 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 274 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 271 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 345 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 303 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, 2.3529411764705883, 0.5714285714285714], "isController": false}, {"data": ["The operation lasted too long: It took 258 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 242 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}, {"data": ["The operation lasted too long: It took 210 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, 1.1764705882352942, 0.2857142857142857], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 350, 85, "The operation lasted too long: It took 251 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, "The operation lasted too long: It took 241 milliseconds, but should not have lasted longer than 200 milliseconds.", 3, "The operation lasted too long: It took 236 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 225 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 395 milliseconds, but should not have lasted longer than 200 milliseconds.", 2], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["GET api/download/256", 50, 2, "The operation lasted too long: It took 457 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 201 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["GET /courses", 50, 42, "The operation lasted too long: It took 236 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 251 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 225 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 241 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 265 milliseconds, but should not have lasted longer than 200 milliseconds.", 2], "isController": false}, {"data": ["GET /notices", 50, 34, "The operation lasted too long: It took 395 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 271 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 303 milliseconds, but should not have lasted longer than 200 milliseconds.", 2, "The operation lasted too long: It took 222 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 285 milliseconds, but should not have lasted longer than 200 milliseconds.", 1], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /login", 50, 7, "The operation lasted too long: It took 322 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 231 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 404 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 345 milliseconds, but should not have lasted longer than 200 milliseconds.", 1, "The operation lasted too long: It took 221 milliseconds, but should not have lasted longer than 200 milliseconds.", 1], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
