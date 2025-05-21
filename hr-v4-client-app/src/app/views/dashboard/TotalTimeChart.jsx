import { observer } from "mobx-react";
import React, { useState, useEffect } from "react";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { toast } from "react-toastify";
import { useStore } from "../../stores";
import { useHistory } from "react-router-dom";
import Highcharts from "highcharts"

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});
export default observer(function DashboardIndex(props) {
  const history = useHistory();
  const [check, setCheck] = useState(false)
  const { dashboardStore } = useStore();
  useEffect(() => {
    highChartsForAdmin();
    //highChartsForUser();
  }) 

  const {
    horizontal,
    highchartReportList
} = dashboardStore;

  const highChartsForAdmin = () => {
    var colors = ['#14af4ade', '#CC0099', "#df631bde"]
    var seriesProject = highchartReportList.map(function(item, index){
      return {
        name : item.project,
        color:colors[index],
        data: item.data
      }
    });
    var categories = horizontal;

    Highcharts.chart('adminContainer', {
      chart: {
        type: 'spline'
      },
      title: {
        text: 'Biểu đồ thể hiện tổng giờ làm từng dự án theo tuần'
      },
      xAxis: {
        // ["Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4", "Tuần 5"]
        categories: categories,
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Total (hours)',
        },
        stackLabels: {
          enabled: true,
          style: {
            fontWeight: 'bold',
            color: ( // theme
              Highcharts.defaultOptions.title.style &&
              Highcharts.defaultOptions.title.style.color
            ) || 'gray'
          }
        }
      },
      tooltip: {
        headerFormat: '<b>{point.x}</b><br/>',
        pointFormat: '{series.name}<br/>Total: {point.y}'
      },
      credits: {
        enabled: false
      },
      plotOptions: {
        column: {
          stacking: 'normal',
          dataLabels: {
            enabled: true
          }
        }
      },
      series: seriesProject
      // [{
      //   name: 'Project 1',
      //   color:'#6666FF',
      //   data: [10, 25, 20,40, 10]
      // },
      // {
      //   name: 'Project 2',
      //   color:'#CC0099',
      //   data: [13, 22, 29, 55, 15]
      // },
      // ]
    });
  }
  
  return (
    <div style={{padding: "0 8px"}}>
      <Grid container className="index-card pt-16" spacing={1}>
        <Grid item md={12} sm={12} xs={12} className="pt-16">
          <div id="adminContainer"></div>
        </Grid>
       
        <Grid item md={12} sm={12} xs={12} className="pt-16">
          <div id="userContainer"></div>
        </Grid>
      </Grid>
    </div>
  );
});
