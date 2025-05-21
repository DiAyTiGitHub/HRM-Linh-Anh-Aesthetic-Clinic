import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { Grid } from "@material-ui/core";
import { toast } from "react-toastify";
import { useStore } from "../../stores";
import Highcharts from "highcharts";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});
export default observer(function DashboardIndex(props) {
  const { dashboardStore } = useStore();
  useEffect(() => {
    highChartsForAdmin();
  });

  const { horizontalList, highchartReportList } = dashboardStore;

  const highChartsForAdmin = () => {
    var colors = [
      "#407F3E",
      "#593E67",
      "#DE741C",
      "#4EB09B",
      "#F8C630",
      "#A10856",
      "#5D2A42",
      "#3376BC",
      "#01877E",
      "#C49A68",
      "#0C5776",
      "#64113F",
    ];
    var seriesProject = highchartReportList?.map(function (item, index) {
      return {
        name: item.project,
        color: colors[index],
        data: item.data,
      };
    });
    var categories = horizontalList;

    Highcharts.chart("adminContainer", {
      chart: {
        type: "spline",
      },
      title: {
        text: "Biểu đồ thể hiện tổng giờ làm từng dự án",
        style: {
          fontFamily: "Times New Roman",
          fontSize: "20px",
          fontWeight: 700,
        },
      },
      xAxis: {
        categories: categories,
      },
      yAxis: {
        min: 0,
        title: {
          text: "Total (hours)",
        },
        stackLabels: {
          enabled: true,
          style: {
            fontWeight: "bold",
            color:
              // theme
              (Highcharts.defaultOptions.title.style &&
                Highcharts.defaultOptions.title.style.color) ||
              "gray",
          },
        },
      },
      tooltip: {
        headerFormat: "<b>{point.x}</b><br/>",
        pointFormat: "{series.name}<br/>Total: {point.y}",
      },
      credits: {
        enabled: false,
      },
      plotOptions: {
        column: {
          stacking: "normal",
          dataLabels: {
            enabled: true,
          },
        },
      },
      series: seriesProject,
    });
  };

  return (
    <div style={{ padding: "0 8px" }}>
      <Grid container className="index-card pt-16" spacing={1}>
        <Grid item md={12} sm={12} xs={12} className="pt-16">
          <div id="adminContainer"></div>
        </Grid>
      </Grid>
    </div>
  );
});
