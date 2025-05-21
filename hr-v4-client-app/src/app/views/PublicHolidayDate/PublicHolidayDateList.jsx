import React, { memo, useEffect } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { getDate } from "app/LocalFunction";
import LocalConstants from "app/LocalConstants";
import CheckIcon from "@material-ui/icons/Check";

function PublicHolidayDateList() {
    const { publicHolidayDateStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        publicHolidayDateList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenView
    } = publicHolidayDateStore;

    const { checkAllUserRoles, isAdmin, isManager } = hrRoleUtilsStore;
    useEffect(() => {
        checkAllUserRoles()
    }, []);
    let columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            render: (rowData) => (
                <div className="flex flex-middle justify-center">
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Xem chi tiết"}
                    >
                        <IconButton
                            className="ml-4"
                            size="small"
                            onClick={() => handleOpenView(rowData?.id)}
                        >
                            <Icon fontSize="small" style={{ color: "green" }}>
                                remove_red_eye
                            </Icon>
                        </IconButton>

                    </Tooltip>

                    {(isManager || isAdmin) && (
                        <Tooltip title="Cập nhật thông tin" placement="top">
                            <IconButton
                                size="small"
                                onClick={() => handleOpenCreateEdit(rowData?.id)}
                            >
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                    {(isManager || isAdmin) && (
                        <Tooltip title="Xóa" placement="top">
                            <IconButton
                                size="small"
                                className="ml-4"
                                onClick={() => handleDelete(rowData)}
                            >
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
            )
        },
        {
            title: t("navigation.publicHolidayDate.holidayDate"),
            field: "holidayDate",
            render: data => <span>{getDate(data?.holidayDate)}</span>,
            align: "left",
            width: "40%",

        },
        {
            title: t("navigation.publicHolidayDate.holidayType"), // allowance.type
            field: "holidayType",
            width: "40%",

            render: data => {
                return <span>{LocalConstants.HolidayLeaveType.getListData().find(i => i.value == data?.holidayType)?.name}</span>
            },
            align: "left",
        },
        {
            title: "Nghỉ nửa ngày",
            field: "isHalfDayOff",
            width: "10%",
            align: "center",
            render: (data) =>
                data?.isHalfDayOff ? <CheckIcon fontSize='small' style={{ color: "green" }} /> : "",
        },
        // {
        //     title:t("navigation.publicHolidayDate.salaryCoefficient") ,
        //     field:"salaryCoefficient" ,
        //     render:data => <span>{data?.salaryCoefficient}</span> ,
        //     align:"left" ,
        //     width:"6%" ,

        // } ,
        // {
        //   title: t("navigation.publicHolidayDate.description"),
        //   field: "description",
        //   render: data => <span>{data?.description}</span>,
        //   align: "left",
        // }
    ];
    return (
        <GlobitsTable
            selection
            data={publicHolidayDateList}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(PublicHolidayDateList));