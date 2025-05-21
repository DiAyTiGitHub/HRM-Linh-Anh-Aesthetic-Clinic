import { Icon, IconButton } from "@material-ui/core";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import CompareArrowsIcon from "@material-ui/icons/CompareArrows";
import InsertInvitationIcon from "@material-ui/icons/InsertInvitation";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import TimerIcon from "@material-ui/icons/Timer";
import { formatDate } from "app/LocalFunction";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { NavLink } from "react-router-dom";
import ConstantList from "../../appConfig";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import LocalConstants, { PositionRelationshipType } from "../../LocalConstants";
import BackspaceIcon from "@material-ui/icons/Backspace";
import { LocalActivity } from "@material-ui/icons";

const ButtonsTable = (props) => {
  const {data} = props;
  const [anchorEl, setAnchorEl] = useState ();
  const {handleDelete, getPositionByStaffId, handleCreateUser, handleOpentFormLeavePosition} =
      useStore ().staffStore;

  return (
      <>
        <IconButton component={NavLink} size='small' to={ConstantList.ROOT_PATH + "staff/profile/" + data.id}>
          <Icon fontSize='small' color='primary'>
            visibility
          </Icon>
        </IconButton>
        <IconButton component={NavLink} size='small' to={ConstantList.ROOT_PATH + "staff/edit/" + data.id}>
          <Icon fontSize='small' color='primary'>
            edit
          </Icon>
        </IconButton>
        <IconButton size='small' onClick={() => handleDelete (data.id)}>
          <Icon fontSize='small' color='secondary'>
            delete
          </Icon>
        </IconButton>

        <IconButton size='small' onClick={() => handleCreateUser (data.id)}>
          <Icon fontSize='small'>person_add</Icon>
        </IconButton>

        <IconButton size='small' onClick={(event) => setAnchorEl (event.currentTarget)}>
          <MoreHorizIcon/>
        </IconButton>

        <Menu
            id='simple-menu'
            anchorEl={anchorEl}
            keepMounted
            open={Boolean (anchorEl)}
            onClose={() => setAnchorEl (null)}>
          {/* <MenuItem component={NavLink} to={ConstantList.ROOT_PATH + "time-sheet-detail/" + data.id}>
          Ngày công
        </MenuItem>

        <MenuItem component={NavLink} to={ConstantList.ROOT_PATH + "staff/TimeSheet/" + data.id}>
          Nhật kí làm việc
        </MenuItem> */}

          {data?.hasPosition && (
              <MenuItem onClick={() => handleOpentFormLeavePosition (data)}>
                <BackspaceIcon style={{color:"orange"}}/>
                <span className='ml-6'>Bãi nhiệm</span>
              </MenuItem>
          )}

          <MenuItem onClick={() => getPositionByStaffId (data.id)}>
            <CompareArrowsIcon style={{color:"green"}}/>
            <span className='ml-6'>Điều chuyển</span>
          </MenuItem>

          <MenuItem component={NavLink} to={ConstantList.ROOT_PATH + "staff-month-schedule-calendar/" + data.id}>
            <InsertInvitationIcon style={{color:"#13529f"}}/>

            <span className='ml-6'>Lịch làm việc</span>
          </MenuItem>

          <MenuItem component={NavLink} to={ConstantList.ROOT_PATH + "time-sheet-detail/" + data?.id}>
            <TimerIcon style={{color:"green"}}/>

            <span className='ml-6'>Lịch sử chấm công</span>
          </MenuItem>
          <MenuItem component={NavLink}
                    to={{
                      pathname:ConstantList.ROOT_PATH + "staff-evaluation-ticket",
                      search:`?id=${data?.id}&name=${data?.displayName}`,
                    }}>
            <LocalActivity style={{color:"green"}}/>

            <span className='ml-6'>Phiếu đánh gía</span>
          </MenuItem>
        </Menu>
      </>
  );
};

function StaffList () {
  const {staffStore} = useStore ();
  const {t} = useTranslation ();

  const {
    pageStaff,
    searchStaff,
    onChangeFormSearch,
    shouldOpenTranserDialog,
    handleSelectListStaff,
    handleChangePage,
    setPageSize,
  } = staffStore;

  let columns = [
    {
      title:t ("general.action"),
      align:"center",
      field:"action",
      minWidth:"100px",
      render:(rowData) => <ButtonsTable data={rowData}/>,
    },
    {
      title:"Mã nhân viên",
      field:"staffCode",
      align:"center",
      render:(rowData) => <span className='px-6'>{rowData?.staffCode}</span>,
    },
    {
      title:"Nhân viên",
      minWidth:"200px",
      render:(rowData) => (
          <>
            {rowData.displayName && (
                <p className='m-0'>
                  <strong>{rowData.displayName}</strong>
                </p>
            )}

            {rowData.birthDate && (
                <p className='m-0'>Ngày sinh: {formatDate ("DD/MM/YYYY", rowData.birthDate)}</p>
            )}

            {rowData.gender && (
                <p className='m-0'>
                  Giới tính: {rowData.gender === "M"? "Nam" : rowData.gender === "F"? "Nữ" : ""}
                </p>
            )}

            {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
          </>
      ),
    },
    {
      title:"Thông tin liên hệ",
      field:"info",
      minWidth:"200px",
      render:(rowData) => (
          <>
            {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}

            {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}

            {/* {rowData.currentResidence && (
            <p className="m-0">Nơi ở hiện tại: {rowData.currentResidence}</p>
          )} */}
          </>
      ),
    },
    {
      title:"Trạng thái nhân viên",
      field:"status.name",
      align:"left",
      minWidth:"150px",
      render:(rowData) => <span className='pr-6'>{rowData?.status?.name}</span>,
    },
    {
      title:"Quản lý trực tiếp",
      align:"left",
      minWidth:"150px",
      render:(rowData) => (
          <>
            {rowData?.currentPosition?.relationships
                ?.filter (
                    (item) =>
                        item?.supervisor?.name &&
                        item?.relationshipType === PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.value
                )
                ?.map ((item, index) => (
                    <>
                      {index > 0 && <br/>}
                      <span className='pr-6'>
                                    - {item?.supervisor?.name}
                        {item?.supervisor?.staff?.displayName
                            ? ` (${item.supervisor.staff.displayName})`
                            : ""}
                                </span>
                    </>
                ))}
          </>
      ),
    },
    {
      title:"Đơn vị",
      field:"organization.name",
      align:"left",
      minWidth:"120px",
      render:(rowData) => <span className='pr-6'>{rowData?.organization?.name}</span>,
    },
    {
      title:"Phòng ban",
      field:"department.name",
      align:"left",
      minWidth:"120px",
      render:(rowData) => (
          <>
            {rowData?.department?.name && <p className='m-0'>{rowData?.department?.name}</p>}
            {rowData?.department?.code && <p className='m-0'>({rowData?.department?.code})</p>}
          </>
      ),
    },
    {
      title:"Chức danh",
      field:"positionTitleName",
      align:"left",
      minWidth:"120px",
      render:(rowData) => <span className='pr-6'>{rowData?.positionTitle?.name}</span>,
    },

    {
      title:"Nơi ở hiện tại",
      field:"currentResidence",
      align:"left",
      minWidth:"180px",
      render:(rowData) => <span className='pr-6'>{rowData?.currentResidence}</span>,
    },
    {
      title:"Mã số BHXH",
      field:"socialInsuranceNumber",
      align:"left",
      minWidth:"120px",
      render:(rowData) => <span className='pr-6'>{rowData?.socialInsuranceNumber}</span>,
    },
    {
      title:"Trạng thái hồ sơ",
      field:"staffDocumentStatus",
      align:"left",
      minWidth:"120px",
      render:(rowData) => (
          <span className='pr-6'>
                    {LocalConstants.StaffDocumentStatus.getListData ().find (
                        (item) => item.value === rowData?.staffDocumentStatus
                    )?.name || ""}
                </span>
      ),
    },
    // {
    //   title: "Loại hợp đồng",
    //   field: "labourAgreementType.name",
    //   align: "left",
    //   minWidth: "150px",
    // },
  ];

  return (
      <GlobitsTable
          selection
          columns={columns}
          data={pageStaff?.content || []}
          totalPages={pageStaff?.totalPages}
          totalElements={pageStaff?.totalElements}
          page={searchStaff?.pageIndex}
          pageSize={searchStaff?.pageSize}
          handleChangePage={handleChangePage}
          setRowsPerPage={setPageSize}
          handleSelectList={handleSelectListStaff}
          pageSizeOption={[10, 25, 50, 100, 200, 500]}
          rowStyle={(rowData, index) => {
            if (rowData?.isOnMaternityLeave) {
              return {backgroundColor:"#ffe6e6"};
            } else {
              return null;
            }
          }}

      />
  );
}

export default memo (observer (StaffList));
