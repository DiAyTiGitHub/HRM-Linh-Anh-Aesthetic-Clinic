import { useStore } from "app/stores";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { observer } from "mobx-react";
import { memo, useState } from "react";

const ReportPieChart = ({ title }) => {
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState({});

  const { reportByMonth, fetchReportByMonth } = useStore().reportStore;

  // Open the modal
  const handleOpen = () => setOpen(true);

  // Calculate the total count
  const total = reportByMonth.reduce((sum, item) => sum + item.totalAmount, 0);

  const styles = {
    metricBox: {
      padding: "15px",
      border: "1px solid #ddd",
      borderRadius: "8px",
      textAlign: "center",
      width: "30%",
      boxShadow: "rgba(0, 0, 0, 0.25) 0px 54px 55px, rgba(0, 0, 0, 0.12) 0px -12px 30px, rgba(0, 0, 0, 0.12) 0px 4px 6px, rgba(0, 0, 0, 0.17) 0px 12px 13px, rgba(0, 0, 0, 0.09) 0px -3px 5px",
      transition: "transform 0.3s ease, box-shadow 0.3s ease",
    },
  };

  // Calculate percentage for each item and set visibility for 0 value items
  const chartData = reportByMonth.map((item) => ({
    id: item.id,
    name: item?.voucherType === 1 ? "Thu" : item?.voucherType === -1 ? "Chi" : "",
    y: total > 0 ? (item.totalAmount / total) * 100 : 0, // Phần trăm
    count: item.totalAmount, // Số lượng thực tế
    visible: item.totalAmount > 0, // Ẩn nếu count bằng 0
    color: item?.voucherType === 1 ? "#28a745" : item?.voucherType === -1 ? "#dc3545" : "#ccc", // Set color for Thu (green) and Chi (red)
  }));

  // Pie chart options configuration
  const options = {
    chart: {
      type: "pie",
    },
    title: {
      text: title,
    },
    credits: {
      enabled: false,
    },
    plotOptions: {
      series: {
        cursor: "pointer",
        point: {
          events: {
            click: function () {
              setSearch({
                pageIndex: 1,
                pageSize: 10,
                statusId: this.options.id,
              });
              handleOpen();
            },
          },
        },
      },
      pie: {
        allowPointSelect: true,
        cursor: "pointer",
        dataLabels: {
          formatter: function () {
            const percentage = this.y.toFixed(1); // Format the value to 1 decimal place
            return percentage.endsWith(".0") ? `${percentage.replace(".0", "")}%` : `${percentage}%`;
          },
          enabled: true, // Enable data labels
          style: {
            fontSize: "16px", // Customize font size
            fontWeight: "bold",
            color: "white", // Label text color
          },
          distance: -30, // Move labels towards the center (negative value)
          verticalAlign: "middle",
          align: "center",
        },
        showInLegend: true,
      },
    },
    series: [
      {
        name: "Phần trăm",
        colorByPoint: true,
        data: chartData,
      },
    ],
    tooltip: {
      pointFormat: "{series.name}: <b>{point.percentage:.1f}%</b><br>Số lượng: <b>{point.count}</b>",
      formatter: function () {
        // Định dạng tiền tệ cho số lượng
        return `${this.series.name}: <b>${this.point.percentage.toFixed(1)}%</b><br>Số lượng: <b>${Highcharts.numberFormat(this.point.count, 0, ',', '.')} VND</b>`;
      },
    },
    legend: {
      labelFormatter: function () {
        const percentage = this.percentage.toFixed(1);
        return this.y === 0 ? `${this.name} (0%)` : `${this.name} (${percentage.replace(/\.0$/, "")}%)`;
      },
    },
  };

  return (
    <div style={{ width: "100%" }}>
      <HighchartsReact highcharts={Highcharts} options={options} />
    </div>
  );
};

export default memo(observer(ReportPieChart));
