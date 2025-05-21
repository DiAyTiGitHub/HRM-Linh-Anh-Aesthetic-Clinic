import React from "react";
import GlobitsTable from "../../../common/GlobitsTable";
import {observer} from "mobx-react";
import {useStore} from "../../../stores";
import {formatDate} from "../../../LocalFunction";

const RecruitmentRequestSummaryTable = () => {
    const {
        payload,
        page,
        setPageIndex,
        setPageSize,
        getRecruitmentRequestSummaries
    } = useStore().recruitmentRequestSummaryStore;

    const columns = [
        {
            title: "Y/c tuyển dụng",
            field: "name",
            align: "left",
        },
        {
            title: "Ngày bắt đầu",
            field: "startDate",
            align: "center",
            render: (rowData) => formatDate("DD/MM/YYYY", rowData?.startDate)
        },
        {
            title: "Ngày kết thúc",
            field: "endDate",
            align: "center",
            render: (rowData) => formatDate("DD/MM/YYYY", rowData?.endDate)
        },
        {
            title: "Số lượng ứng viên",
            field: "totalCandidates",
            align: "center",
        },
        {
            title: "Số lượng gửi Offer",
            field: "candidatesSentOfferMail",
            align: "center",
        },
        {
            title: "Số lượng đã phỏng vấn",
            field: "candidatesWithResultStatus",
            align: "center",
        },
    ];

    const handleChangeTablePageIndex = (event, newPage) => {
        setPageIndex(newPage);
        getRecruitmentRequestSummaries({
            ...payload,
            pageIndex: newPage
        });
    };

    const handleChangeTablePageSize = (event) => {
        const newSize = event.target.value;
        setPageSize(newSize);
        getRecruitmentRequestSummaries({
            ...payload,
            pageIndex: 1,
            pageSize: newSize
        });
    };

    return (
        <GlobitsTable
            data={page.content}
            columns={columns}
            totalPages={page.totalPages}
            handleChangePage={handleChangeTablePageIndex}
            setRowsPerPage={handleChangeTablePageSize}
            pageSize={payload.pageSize}
            pageSizeOption={[10, 25, 50, 100, 200, 500]}
            totalElements={page.totalElements}
            page={payload.pageIndex}
        />
    );
};

export default observer(RecruitmentRequestSummaryTable);
