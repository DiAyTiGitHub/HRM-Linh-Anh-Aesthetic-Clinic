import React , { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton , Icon , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import LocalConstants from "../../LocalConstants";

// function MaterialButton(props) {
//   const { item } = props;

//   return (
//     <div>
//       <IconButton size="small" onClick={() => props.onSelect(item, 0)}>
//         <Icon fontSize="small" color="primary">
//           edit
//         </Icon>
//       </IconButton>
//       <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
//         <Icon fontSize="small" color="secondary">
//           delete
//         </Icon>
//       </IconButton>
//     </div>
//   );
// }

function StaffLabourAgreementList() {
    const {staffLabourAgreementStore} = useStore();
    const {t} = useTranslation();

    const {
        listStaffLabourAgreement ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        handleExportHDLD ,
        handleSetOpenPreviewPopup ,
        setSelectedStaffLabourAgreement
    } = staffLabourAgreementStore;

    const columns = [
        {
            title:t("general.action") ,
            minWidth:"48px" ,
            align:"center" ,
            // render: (rowData) => (
            //   <MaterialButton
            //     item={rowData}
            //     onSelect={(rowData, method) => {
            //       if (method === 0) {
            //         handleOpenCreateEdit(rowData?.id);
            //       } else if (method === 1) {
            //         handleDelete(rowData);
            //       } else {
            //         alert("Call Selected Here:" + rowData?.id);
            //       }
            //     }}
            //   />
            // ),
            render:(rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Cập nhật thông tin" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleOpenCreateEdit(rowData?.id);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Xóa" placement="top">
                            <IconButton
                                size="small"
                                className="ml-4"
                                onClick={() => handleDelete(rowData)
                                }
                            >
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Tải xuống hợp đồng lao động" arrow>
                            <IconButton
                                size="small"
                                className="ml-4"
                                onClick={() => handleExportHDLD(rowData?.id)}
                            >
                                <Icon fontSize="small" color="blue">
                                    description
                                </Icon>
                            </IconButton>
                        </Tooltip>
                        <Tooltip title="Xem trước hợp đồng lao động" arrow>
                            <IconButton
                                size="small"
                                className="ml-4"
                                onClick={() => {
                                    handleSetOpenPreviewPopup(true);
                                    setSelectedStaffLabourAgreement(rowData);
                                }}
                            >
                                <Icon fontSize='small' color='primary'>
                                    visibility
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            } ,
        } ,
        {
            title:t("agreements.labourAgreementNumber") ,
            field:"labourAgreementNumber" ,
            align:"left" ,
        } ,
        {
            title:"Nhân viên áp dụng" ,
            field:"staff" ,
            align:"left" ,
            render:data => <span>{data?.staff?.displayName}</span>
        } ,
        {
            title:t("agreements.signedDate") ,
            field:"signedDate" ,
            align:"left" ,
            render:data => data?.signedDate && (<span>{formatDate("DD/MM/YYYY" , data?.signedDate)}</span>)
        } ,
        {
            title:t("agreements.startDate") ,
            field:"startDate" ,
            align:"left" ,
            render:data => data?.startDate && (<span>{formatDate("DD/MM/YYYY" , data?.startDate)}</span>)
        } ,
        {
            title:t("agreements.endDate") ,
            field:"endDate" ,
            align:"left" ,
            render:data => data?.endDate && (<span>{formatDate("DD/MM/YYYY" , data?.endDate)}</span>)
        } ,
        {
            title:t("agreements.contractType") ,
            field:"contractType" ,
            align:"left" ,
            render:data => <span>{data?.contractType?.name}</span>
        } ,
        {
            title:"Trạng thái hợp đồng" ,
            field:"agreementStatus" ,
            align:"left" ,
            render:row => {
                return <span>{LocalConstants.AgreementStatus.getListData().find(i => i.value == row?.agreementStatus)?.name}</span>
            }
        }
    ];

    return (
        <GlobitsTable
            selection
            data={listStaffLabourAgreement}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10 , 15 , 25 , 50 , 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(StaffLabourAgreementList));
