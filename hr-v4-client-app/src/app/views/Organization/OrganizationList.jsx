import { Icon , IconButton , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import React , { memo , useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { getDate } from "app/LocalFunction";

function OrganizationList() {
    const {t} = useTranslation();

    const {
        organizationStore , hrRoleUtilsStore
    } = useStore();

    const {
        listOrganizations ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        handleOpenView
    } = organizationStore;

    const {
        isAdmin ,
        checkAllUserRoles,
        isManager
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    } , []);

    const columns = [
        {
            title:t("general.action") ,
            width:"10%" ,
            render:(rowData) => (
                <div className="flex flex-center">
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
                            <Icon fontSize="small" style={{color:"green"}}>
                                remove_red_eye
                            </Icon>
                        </IconButton>

                    </Tooltip>
                    {(isAdmin || isManager) && (
                        <Tooltip
                            arrow
                            title={"Cập nhật thông tin thành phần lương"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData?.id)}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                    {(isAdmin || isManager) && (
                        <Tooltip
                            arrow
                            title={"Xóa thông tin thành phần lương"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleDelete(rowData)}>
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
            ) ,
        } ,
        {
            title:"Mã" ,
            field:"code" ,
            align:"center" ,
            width:"20%" ,
        } ,

        {
            title:"Tên đơn vị" ,
            field:"name" ,
            align:"center" ,
            width:"20%" ,
        } ,

        {
            title:"Mã số thuế" ,
            field:"taxCode" ,
            align:"center" ,
            width:"20%" ,
        } ,
        {
            title:"Ngày thành lập" ,
            field:"foundedDate" ,
            align:"center" ,
            render:row => {
                return <span>{getDate(row?.foundedDate)}</span>
            }
        } ,
        {
            title:"Người đại diện" ,
            field:"representative" ,
            align:"center" ,
            render:row => <span className="px-2">
        {`${row?.representative?.displayName || ''} - ${row?.representative?.staffCode || ''}`}
      </span>
        } ,


        // {
        //   title: "đơn vị trực thuộc",
        //   field: "organization",
        //   render: (rowData) => (
        //     <span>
        //       {rowData?.parent?.name}
        //     </span>
        //   ),
        // },
        // {
        //   title: "Website",
        //   field: "website",
        //   width: "20%",
        //   ...Config.tableCellConfig,
        // },
    ];

    return (
        <GlobitsTable
            selection
            data={listOrganizations}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10 , 15 , 25 , 50 , 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
            colParent
        />
    );
}

export default memo(observer(OrganizationList));
