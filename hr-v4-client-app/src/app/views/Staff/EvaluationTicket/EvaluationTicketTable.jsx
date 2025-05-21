import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { EVALUATION_STATUS } from "../../../LocalConstants";
import { formatDate } from "../../../LocalFunction";
import ConstantList from "../../../appConfig";
import GlobitsTable from "../../../common/GlobitsTable";
import { useStore } from "../../../stores";
import { useRef, useState } from "react";
import EvaluationTicketPrint from "app/views/Salary/Print/EvaluationTicketPrint";
import { useReactToPrint } from "react-to-print";
export default observer(function EvaluationTicketTable() {
    const { t } = useTranslation();
    const history = useHistory();
    const {
        page,
        handleChangePage,
        setPageSize,
        deleteEvaluationFormById,
        exportEvaluationFormWord,
        handleSelected,
        getEvaluationFormsById,
    } = useStore().evaluationTicketStore;
    let columns = [
        {
            title: t("general.action"),
            align: "center",
            field: "action",
            minWidth: "100px",
            render: (rowData) => (
                <div className='flex flex-middle justify-center'>
                    <Tooltip title='Chỉnh sửa phiếu' placement='top'>
                        <IconButton
                            className='bg-white'
                            size='small'
                            onClick={() =>
                                history.push(ConstantList.ROOT_PATH + `staff-evaluation-ticket/edit/${rowData?.id}`)
                            }>
                            <Icon fontSize='small' color='primary'>
                                edit
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title='Xóa phiếu' placement='top'>
                        <IconButton
                            className='bg-white'
                            size='small'
                            onClick={() => deleteEvaluationFormById(rowData.id)}>
                            <Icon fontSize='small' color='secondary'>
                                delete
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title='Tải phiếu' placement='top'>
                        <IconButton
                            className='bg-white'
                            size='small'
                            onClick={() => exportEvaluationFormWord(rowData.id)}>
                            <Icon fontSize='small' style={{ color: "gray" }}>
                                description
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title='In Phiếu' placement='top'>
                        <IconButton className='bg-white' size='small' onClick={() => handlePrint(rowData.id)}>
                            <Icon fontSize='small' style={{ color: "gray" }}>
                                print
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div>
            ),
        },
        {
            title: "Mã nhân viên",
            field: "staffCode",
            minWidth: "100px",
            render: (rowData) => <span className='px-6'>{rowData?.staffCode}</span>,
        },
        {
            title: "Chức danh",
            field: "positionTitleName",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.positionTitle?.name}</span>,
        },
        {
            title: "Nhân viên",
            minWidth: "200px",
            render: (rowData) => (
                <>
                    {rowData.staffName && (
                        <p className='m-0'>
                            <strong>{rowData.staffName}</strong>
                        </p>
                    )}
                </>
            ),
        },
        {
            title: "Ngày nhận việc",
            field: "hireDate",
            align: "center",
            minWidth: "150px",
            render: (rowData) => <span className='pr-6'>{formatDate("DD/MM/YYYY", rowData?.hireDate)}</span>,
        },
        {
            title: "Thời hạn HĐLĐ trước",
            field: "previousContractDuration",
            align: "center",
            minWidth: "150px",
            render: (rowData) => (
                <span className='pr-6'>{formatDate("DD/MM/YYYY", rowData?.previousContractDuration)}</span>
            ),
        },
        {
            title: "Loại HĐLĐ",
            field: "contractTypeName",
            minWidth: "120px",
        },
        {
            title: "Trạng thái",
            field: "status",
            minWidth: "120px",
            render: (rowData) => (
                <>
                    {rowData?.status && (
                        <p className='m-0'>{rowData?.status === EVALUATION_STATUS.PASS ? "Đạt" : "Không đạt"}</p>
                    )}
                </>
            ),
        },
    ];
    const [printData, setPrintData] = useState(null);
    const componentRef = useRef(null);

    const handlePrePrint = useReactToPrint({
        content: () => componentRef.current,
        documentTitle: "Phiếu đánh giá",
        onAfterPrint: () => setPrintData(null),
    });

    async function handlePrint(ticketId) {

        if (ticketId) {
            try {
                const form = await getEvaluationFormsById(ticketId);
                setPrintData(form);
                handlePrePrint();
            } catch (error) {
                console.error("Error fetching salary data:", error);
            }
        }
    }

    return (
        <>
            <GlobitsTable
                columns={columns}
                data={page?.content || []}
                totalPages={page?.totalPages}
                totalElements={page?.totalElements}
                page={page?.pageIndex}
                pageSize={page?.pageSize}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                selection
                handleSelectList={handleSelected}
            />

            <EvaluationTicketPrint printData={printData} componentRef={componentRef} />
        </>
    );
});
