import React from "react";
import {Box, Typography} from "@material-ui/core";

export default function VoucherSummaryBalance({data}) {
    const formatDate = (dateString) => {
        if (!dateString) return ""; // Trả về chuỗi rỗng nếu không có giá trị
        const date = new Date(dateString);
        return isNaN(date.getTime()) ? "" : date.toLocaleDateString("vi-VN", {
            day: "2-digit", month: "2-digit", year: "numeric"
        });
    };

    const formatCurrency = (amount) => {
        if (amount == null || isNaN(amount)) return "0 VND";
        return (amount < 0 ? "-" : "") + Math.abs(amount).toLocaleString("vi-VN", {
            style: "currency", currency: "VND",
        });
    };

    const getTotalIncome = (transactions) => {
        if (!transactions || !Array.isArray(transactions)) return 0;
        return transactions
            .filter((item) => item.voucherType > 0)
            .reduce((sum, item) => sum + item.totalAmount * item.voucherType, 0);
    };

    const getTotalExpense = (transactions) => {
        if (!transactions || !Array.isArray(transactions)) return 0;
        return transactions
            .filter((item) => item.voucherType < 0)
            .reduce((sum, item) => sum + item.totalAmount * item.voucherType, 0);
    };

    return (<Box sx={{p: 2, borderRadius: 2, bgcolor: "#f9f9f9", boxShadow: 1}}>
        <Typography variant="h6" fontWeight="bold" gutterBottom>
            Số dư của quỹ: {data?.budget?.name || "Không xác định"}
        </Typography>

        <Typography variant="body1" color={data?.summaryUntilToDate?.totalAmount < 0 ? "error" : "success.main"}>
            {data?.summaryUntilToDate?.totalAmount < 0 ? "Đang âm " : "Đang có "}
            <strong>{formatCurrency(data?.summaryUntilToDate?.totalAmount)}</strong>
        </Typography>

        <Typography variant="body2" color="textSecondary">
            Tính tới ngày <strong>{formatDate(data?.summaryUntilToDate?.toDate)}</strong>
        </Typography>

        <Box mt={2} p={2} bgcolor="white" borderRadius={2} boxShadow={1}>
            <Typography variant="body1" color="success.main">
                Tổng thu: <strong>{formatCurrency(getTotalIncome(data?.summaryFromDateToDate))}</strong>
            </Typography>

            <Typography variant="body1" color="error">
                Tổng chi: <strong>{formatCurrency(getTotalExpense(data?.summaryFromDateToDate))}</strong>
            </Typography>
        </Box>

        <Typography variant="body2" color="textSecondary" mt={2}>
            Khoảng thời
            gian: <strong>{formatDate(data?.summaryFromDateToDate?.[0]?.fromDate)}</strong> đến <strong>{formatDate(data?.summaryFromDateToDate?.[0]?.toDate)}</strong>
        </Typography>
    </Box>);
}
