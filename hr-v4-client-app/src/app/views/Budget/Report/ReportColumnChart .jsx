import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { observer } from "mobx-react";
import { memo } from "react";
import { useStore } from "app/stores";

const ReportColumnChart = ({ title }) => {
  const { reportByYear } = useStore().reportStore;

  // Xử lý dữ liệu
  const months = reportByYear.map((item) => "Tháng " + item.month);
  const incomeData = reportByYear.map((item) => item.income);
  const expenditureData = reportByYear.map((item) => item.expenditure);

  // Cấu hình biểu đồ
  const options = {
    chart: {
      type: "column", // Biểu đồ dạng cột
    },
    title: {
      text: title, // Tiêu đề biểu đồ
    },
    xAxis: {
      categories: months, // Danh sách tháng trên trục x
      crosshair: true, // Kẻ dọc khi hover
      title: {
        text: "Tháng",
      },
    },
    yAxis: {
      min: 0,
      title: {
        text: "Giá trị (VND)", // Đơn vị trên trục y
      },
    },
    tooltip: {
      shared: true, // Hiển thị tooltip chung cho cả 2 cột
      formatter: function () {
        return `
          <b>${this.x}</b><br>
          <span style="color:${this.points[0].color}">●</span> Thu (Income): 
          <b>${Highcharts.numberFormat(this.points[0].y, 0, ',', '.')} VND</b><br>
          <span style="color:${this.points[1].color}">●</span> Chi (Expenditure): 
          <b>${Highcharts.numberFormat(this.points[1].y, 0, ',', '.')} VND</b>
        `;
      },
    },
    plotOptions: {
      column: {
        pointPadding: 0.2, // Khoảng cách giữa các cột
        borderWidth: 0, // Bỏ viền cột
      },
    },
    series: [
      {
        name: "Thu (Income)", // Cột Thu
        data: incomeData,
        color: "#4B8FF6", // Màu xanh
      },
      {
        name: "Chi (Expenditure)", // Cột Chi
        data: expenditureData,
        color: "#FF6F61", // Màu đỏ
      },
    ],
    legend: {
      align: "center",
      verticalAlign: "bottom",
    },
    credits: {
      enabled: false, // Tắt logo Highcharts
    },
  };

  return (
    <div style={{ width: "100%" }}>
      <HighchartsReact highcharts={Highcharts} options={options} />
    </div>
  );
};

export default memo(observer(ReportColumnChart));
