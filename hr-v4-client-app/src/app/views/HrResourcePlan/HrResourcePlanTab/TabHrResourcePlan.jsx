import { Icon, IconButton, Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import { getDate } from "date-fns";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";

function TabHrResourcePlan() {
    const { hrResourcePlanStore } = useStore();
    const { t } = useTranslation();

    const {
        hrResourcePlanList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
    } = hrResourcePlanStore;

    let columns = [
        {
            title: t("general.action"),
            width: "6%",
            align: "center",
            render: (rowData) => {
                return (
                    <div className='flex flex-middle justify-center'>
                        <Tooltip title='Cập nhật thông tin' placement='top'>
                            <IconButton
                                size='small'
                                onClick={function () {
                                    handleOpenCreateEdit(rowData?.id);
                                }}>
                                <Icon fontSize='small' color='primary'>
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title='Xóa' placement='top'>
                            <IconButton size='small' className='ml-4' onClick={() => handleDelete(rowData)}>
                                <Icon fontSize='small' color='secondary'>
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: t("Tên định biên"),
            field: "name",
            align: "left",
        },
        {
            title: t("Mã định biên"),
            field: "code",
            align: "left",
        },
        {
            title: t("Mô tả"),
            field: "description",
            align: "left",
        },
        {
            title: t("Ngày lập định biên"),
            field: "planDate",
            align: "left",
            render: (data) => data?.planDate && <span>{formatDate("DD/MM/YYYY", data?.planDate)}</span>,
        },
    ];

    return (
        <GlobitsTable
            selection
            data={hrResourcePlanList}
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
export default memo(observer(TabHrResourcePlan));
