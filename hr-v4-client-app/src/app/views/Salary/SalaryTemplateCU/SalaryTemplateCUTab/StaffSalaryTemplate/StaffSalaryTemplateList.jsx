import React from "react";
import { useTranslation } from "react-i18next";
import { Icon , IconButton , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { useStore } from "app/stores";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";

export default observer(function StaffSalaryTemplateList() {
    const {popupStaffSalaryTemplateStore , hrRoleUtilsStore , salaryTemplateStore} = useStore();
    const {t} = useTranslation();
    const {
        listStaffSalaryTemplate ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleOpenEdit ,
        handleDelete ,
        setPageSize ,
        handleChangePage ,
        handleSelectListDelete ,
        handleOpenView
    } = popupStaffSalaryTemplateStore;

    const {
        isAdmin ,
        isManager
    } = hrRoleUtilsStore;
    const {
        openViewPopup:readOnly ,
    } = salaryTemplateStore;
    let columns = [
        {
            title:t("STT") ,
            width:"80" ,
            minWidth:"100px" ,
            render:(rowData , index) => rowData?.tableData?.id + 1 ,
            cellStyle:{textAlign:"center"} ,
            headerStyle:{textAlign:"center"} ,
            hidden:!readOnly
        } ,
        {
            title:t("general.action") ,
            minWidth:"150px" ,
            render:(rowData) => (
                <div className="flex flex-middle justify-center">
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Xem chi tiết"}
                    >
                        <IconButton size="small" onClick={() => handleOpenView(rowData)}>
                            <Icon fontSize="small" style={{color:"green"}}>
                                remove_red_eye
                            </Icon>
                        </IconButton>

                    </Tooltip>
                    {((isAdmin || isManager) && !readOnly) && (
                        <Tooltip title="Cập nhật thông tin" placement="top">
                            <IconButton size="small" onClick={() => handleOpenEdit(rowData)}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}

                    {((isAdmin || isManager) && !readOnly) && (
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
            ) ,
            cellStyle:{textAlign:"center"} ,
            headerStyle:{textAlign:"center"} ,
        } ,
        {
            title:"Mã nhân viên" ,
            field:"staffCode" ,
            align:"center" ,
            minWidth:"150px" ,
            render:(rowData) => <span className='px-6'>{rowData?.staff?.staffCode}</span> ,
        } ,
        {
            title:"Nhân viên" ,
            // align: "center",
            minWidth:"150px" ,
            render:(rowData) => (
                <div className="pr-6">
                    {rowData?.staff?.displayName && (
                        <p className='m-0 no-wrap-text'>
                            <strong>{rowData?.staff?.displayName}</strong>
                        </p>
                    )}

                    {rowData?.staff?.birthDate && (
                        <p className='m-0 no-wrap-text'>Ngày
                            sinh: {formatDate("DD/MM/YYYY" , rowData?.staff?.birthDate)}</p>
                    )}

                    {rowData?.staff?.gender && (
                        <p className='m-0 no-wrap-text'>
                            Giới
                            tính: {rowData?.staff?.gender === "M" ? "Nam" : rowData?.staff?.gender === "F" ? "Nữ" : ""}
                        </p>
                    )}

                    {rowData?.staff?.birthPlace &&
                        <p className='m-0 no-wrap-text'>Nơi sinh: {rowData?.staff?.birthPlace}</p>}
                </div>
            ) ,
        } ,

        {
            title:"Đơn vị" ,
            field:"staff.organization.name" ,
            width:"10%" ,
            minWidth:"150px" ,
            align:"left" ,
        } ,
        {
            title:"Phòng ban" ,
            field:"department.name" ,
            width:"10%" ,
            align:"left" ,
            minWidth:"150px" ,
            render:(rowData) => (
                <>
                    {rowData?.staff?.department?.name && <p className='m-0'>{rowData?.staff?.department?.name}</p>}
                    {rowData?.staff?.department?.code && <p className='m-0'>({rowData?.staff?.department?.code})</p>}
                </>
            ) ,
        } ,
        {
            title:"Chức danh" ,
            field:"staff.positionTitle.name" ,
            width:"10%" ,
            minWidth:"150px" ,
            align:"left" ,
        } ,

        {
            title:"Ngày bắt đầu" ,
            field:"fromDate" ,
            width:"20%" ,
            align:"center" ,
            minWidth:"150px" ,
            render:(rowData) => (
                <span className="text-center">
                    {formatDate("DD/MM/YYYY" , rowData?.fromDate)}
                </span>
            )

        } ,
        {
            title:"Ngày kết thúc" ,
            field:"toDate" ,
            width:"20%" ,
            align:"center" ,
            minWidth:"150px" ,
            render:(rowData) => (
                <span className="text-center">
                    {formatDate("DD/MM/YYYY" , rowData?.toDate)}
                </span>
            )

        }
    ];

    return (
        <GlobitsTable
            selection={!readOnly}
            handleSelectList={handleSelectListDelete}
            data={listStaffSalaryTemplate}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10 , 25 , 50, 100, 200]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
});
