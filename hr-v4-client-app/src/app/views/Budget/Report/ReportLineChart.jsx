import { useStore } from "app/stores";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { observer } from "mobx-react";
import { memo } from "react";

const ReportLineChart = ({ title }) => {
  const { reportByYear } = useStore().reportStore;

  // Process the data into series for the chart
  const months = reportByYear.map((item) => "Tháng " + item.month);
  const incomeData = reportByYear.map((item) => item.income);
  const expenditureData = reportByYear.map((item) => item.expenditure);

  const options = {
    chart: {
      type: "line", // Line chart
    },
    credits: {
      enabled: false,
    },
    title: {
      text: title, // Use passed title
    },
    xAxis: {
      categories: months, // Months will be displayed on the x-axis
      title: {
        text: "Tháng",
      },
    },
    yAxis: {
      min: 0,
      title: {
        text: "Giá trị (VND)",
      },
    },
    legend: {
      align: "center",
      verticalAlign: "bottom",
      y: 0,
      symbolHeight: 10,
    },
    tooltip: {
      headerFormat: "<b>{point.key}</b><br>",
      pointFormat: `
        <span style="color:{series.color}">{series.name}</span>: 
        <b>{point.y}</b> VND<br/>`,
    },
    plotOptions: {
      series: {
        cursor: "pointer",
        point: {
          events: {
            click: function () {
              console.log("Series Code:", this.series.options.code);
            },
          },
        },
      },
      line: {
        dataLabels: {
          enabled: true, // Show data labels for line chart
        },
        enableMouseTracking: true,
      },
    },
    series: [
      {
        name: "Thu (Income)", // Series for income
        data: incomeData,
        code: "income",
        color: "#4B8FF6", // Income color (blue)
        marker: {
          symbol: "circle", // Show circle markers on the line
        },
      },
      {
        name: "Chi (Expenditure)", // Series for expenditure
        data: expenditureData,
        code: "expenditure",
        color: "#FF6F61", // Expenditure color (red)
        marker: {
          symbol: "circle", // Show circle markers on the line
        },
      },
    ],
  };

  return (
    <div>
      <HighchartsReact highcharts={Highcharts} options={options} />
    </div>
  );
};

export default memo(observer(ReportLineChart));
