import { useStore } from 'app/stores'
import { observer } from 'mobx-react'
import React, { memo } from 'react'
import { useTranslation } from 'react-i18next'
import GlobitsTable from 'app/common/GlobitsTable'
import { formatDate } from 'app/LocalFunction'

function CIRRecruitmentRoundSummary() {
    const { t } = useTranslation();

    const { recruitmentStore } = useStore();
    const {
        selectedRecruitment
    } = recruitmentStore;

    const columns = [
        {
            title: "Thứ tự",
            align: "center",
            width: "6%",
            field: "roundOrder",
        },
        {
            title: "Tên vòng",
            align: "center",
            width: "30%",
            field: "name",
        },
        {
            title: "Loại kiểm tra",
            align: "center",
            width: "10%",
            field: "examType.name",
        },
        {
            title: "Ngày diễn ra",
            align: "center",
            width: "10%",
            field: "takePlaceDate",
            render: (rowData) => (
                <span>
                    {rowData?.takePlaceDate && (formatDate("DD/MM/YYYY", rowData?.takePlaceDate))}
                </span>
            ),
        },
        {
            title: "Địa điểm tổ chức",
            align: "center",
            width: "20%",
            field: "interviewLocation",
        }, {
            title: "Ghi chú",
            align: "center",
            width: "25%",
            field: "description",
        },
    ];


    return (
        <GlobitsTable
            data={selectedRecruitment?.recruitmentRounds || []}
            columns={columns}
            nonePagination
        />
    );
}

export default memo(observer(CIRRecruitmentRoundSummary));