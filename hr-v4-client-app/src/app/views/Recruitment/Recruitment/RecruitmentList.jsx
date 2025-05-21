import React, {memo} from "react";
import {observer} from "mobx-react";
import {useTranslation} from "react-i18next";
import {Icon, IconButton, Tooltip} from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {getDate} from "app/LocalFunction";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import FormatListNumberedIcon from '@material-ui/icons/FormatListNumbered';
import HourglassEmptyIcon from '@material-ui/icons/HourglassEmpty';


function RecruitmentList() {
    const history = useHistory();
    const {recruitmentStore} = useStore();
    const {t} = useTranslation();

    const {
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        listRecruitments,
        handleSelectListDelete
    } = recruitmentStore;

    let columns = [
        {
            title: t("general.action"),
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
            align: "center",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Cập nhật thông tin" placement="top">
                            <IconButton size="small" onClick={function () {
                                history.push("/recruitment/" + rowData?.id);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Xóa đợt tuyển" placement="top">
                            <IconButton
                                size="small"
                                className="ml-4"
                                onClick={() => handleDelete(rowData?.id)
                                }
                            >
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        {rowData?.numberAppliedCandidates > 0 && (
                            <Tooltip title="Danh sách hồ sơ ứng tuyển" placement="top">
                                <IconButton
                                    className="ml-4"
                                    size="small"
                                    style={{color: "green"}}
                                    onClick={() => {
                                        history.push("/candidates-in-recruitment/" + rowData?.id);
                                    }}
                                >
                                    <FormatListNumberedIcon/>
                                </IconButton>
                            </Tooltip>
                        )}

                        {rowData?.numberAppliedCandidates > 0 && (
                            <Tooltip title="Quá trình tuyển dụng" placement="top">
                                <IconButton
                                    className="ml-4"
                                    size="small"
                                    style={{color: "orange"}}
                                    onClick={() => {
                                        history.push("/recruitment-process/" + rowData?.id);
                                    }}
                                >
                                    <HourglassEmptyIcon/>
                                </IconButton>
                            </Tooltip>
                        )}
                    </div>
                );
            },
        },
        {
            title: "Mã đợt tuyển dụng",
            field: "code",
            align: "left",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
        },
        {
            title: "Tên đợt tuyển dụng",
            field: "name",
            align: "left",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
        },
        {
            title: "Kế hoạch thực hiện",
            field: "recruitmentPlan.name",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
            align: "left",
        },
        {
            title: "Từ ngày",
            field: "startDate",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
            align: "center",
            render: row => <span>{getDate(row?.startDate)}</span>
        },
        {
            title: "Đến ngày",
            field: "endDate",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
            align: "center",
            render: row => <span>{getDate(row?.endDate)}</span>
        },
        {
            title: "SL ứng tuyển",
            field: "numberAppliedCandidates",
            align: "center",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
        },
        {
            title: "Đơn vị tuyển dụng",
            field: "organization.name",
            align: "left",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
        },
        {
            title: "Phòng ban tuyển dụng",
            field: "department.name",
            align: "left",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
        },
        {
            title: "Chức danh cần tuyển",
            field: "positionTitle.name",
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
            align: "left",
        },
    ];

    return (
        <GlobitsTable
            data={listRecruitments}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
            selection
            handleSelectList={handleSelectListDelete}
        />
    );
}

export default memo(observer(RecruitmentList));
