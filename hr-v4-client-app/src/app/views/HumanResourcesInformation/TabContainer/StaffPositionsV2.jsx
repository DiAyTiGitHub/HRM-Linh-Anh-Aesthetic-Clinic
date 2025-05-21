import { Button, ButtonGroup, Grid, Icon, IconButton, Tooltip } from "@material-ui/core";
import CheckIcon from "@material-ui/icons/Check";
import TouchAppIcon from "@material-ui/icons/TouchApp";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import PositionCUForm from "app/views/Position/PositionCUForm";
import SelectMulPositionsPopup
  from "app/views/HumanResourcesInformation/Component/SelectMultiplePositions/SelectMulPositionsPopup";
import { observer } from "mobx-react";
import { memo, useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import "react-toastify/dist/ReactToastify.css";
import GlobitsConfirmationDialog from "../../../common/GlobitsConfirmationDialog";
import localStorageService from "../../../services/localStorageService";
import StaffWorkingHistoryV2 from "./StaffWorkingHistoryV2";

function StaffPositionsV2 () {
  const {t} = useTranslation ();
  const {id} = useParams ();

  const {positionStore, staffStore, staffWorkingHistoryStore} = useStore ();

  const isAdmin = useMemo (() => {
    let roles = localStorageService.getLoginUser ()?.user?.roles?.map ((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
    return roles.some ((role) => auth.includes (role));
  }, []);

  const {selectedStaff} = staffStore;

  const {
    handleClose,
    openCreateEditPopup,
    handleOpenSelectMultiplePopup,
    openSelectMultiplePopup,
    handleOpenConfirmRemoveFromPosPopup,
    openConfirmRemoveFromPosPopup,
    handleRemoveStaffFromPosition,
    handleOpenCreateEdit,
    handleAssignPositionsForStaff,

    handleCloseConfirmAssignPopup,
    openConfirmAssignPopup,
    currentPositions,
    setCurrentPositions,
    fetchPositions,
  } = positionStore;

  const {pagingStaffWorkingHistory} = staffWorkingHistoryStore;

  useEffect (() => {
    if (id) {
      fetchPositions (id);
    }
  }, []);

  async function handleConfirmAssign () {
    try {
      const response = await handleAssignPositionsForStaff (id);

      if (!response) throw new Error ();

      await fetchPositions (id);
      await pagingStaffWorkingHistory ({staffId:id});
      handleCloseConfirmAssignPopup ();
      handleClose ();
    } catch (error) {
      console.error (error);
    }
  }

  async function handleConfirmRemove () {
    try {
      const response = await handleRemoveStaffFromPosition ();

      if (!response) throw new Error ();

      await fetchPositions (id);
      await pagingStaffWorkingHistory ({staffId:id});
      handleCloseConfirmAssignPopup ();
      handleClose ();
    } catch (error) {
      console.error (error);
    }
  }

  const columns = [
    {
      title:t ("general.action"),
      width:"5%",
      render:(rowData) => {
        return (
            <>
              {isAdmin && (
                  <div className='flex flex-middle justify-center'>
                    <Tooltip title='Cập nhật thông tin' placement='top'>
                      <IconButton size='small' onClick={() => handleOpenCreateEdit (rowData?.id)}>
                        <Icon fontSize='small' color='primary'>
                          edit
                        </Icon>
                      </IconButton>
                    </Tooltip>

                    <Tooltip title='Bãi nhiệm' placement='top'>
                      <IconButton
                          size='small'
                          className='ml-4'
                          onClick={() => handleOpenConfirmRemoveFromPosPopup (rowData)}>
                        <Icon fontSize='small' style={{color:"orange"}}>
                          disabled_by_default
                        </Icon>
                      </IconButton>
                    </Tooltip>
                  </div>
              )}

              {!isAdmin && (
                  <Tooltip placement='top' title='Chi tiết thông tin'>
                    <IconButton size='small' onClick={() => handleOpenCreateEdit (rowData?.id)}>
                      <Icon fontSize='small' color='primary'>
                        remove_red_eye
                      </Icon>
                    </IconButton>
                  </Tooltip>
              )}
            </>
        );
      },
      align:"center",
    },
    {
      title:"Mã vị trí",
      field:"code",
      width:"10%",
      render:(row) => <span>{row?.code}</span>,
      align:"center",
    },
    {
      title:"Tên vị trí",
      field:"title",
      width:"20%",
      render:(row) => <span>{row?.name}</span>,
      align:"center",
    },
    {
      title:"Chức danh",
      field:"title",
      width:"20%",
      render:(data) => <span className='px-4'>{`${data?.title?.name}`}</span>,
      align:"center",
    },
    {
      title:"Đơn vị",
      field:"organization",
      width:"15%",
      render:(row) => <span>{row?.department?.organization?.name}</span>,
      align:"center",
    },
    {
      title:"Phòng ban",
      field:"department",
      width:"15%",
      render:(row) => <span>{row?.department?.name}</span>,
      align:"center",
    },

    {
      title:"Vị trí chính",
      field:"isMain",
      width:"10%",
      align:"center",
      render:(data) => {
        if (data?.isMain) return <CheckIcon fontSize='small' style={{color:"green"}}/>;
        return "";
      },
    },
    {
      title:"Vị trí kiêm nhiệm",
      field:"isConcurrent",
      width:"10%",
      align:"center",
      render:(data) => {
        if (data?.isConcurrent) return <CheckIcon fontSize='small' style={{color:"green"}}/>;
        return "";
      },
    },
  ];

  return (
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <TabAccordion title={"Vị trí công tác"}>
            <Grid container spacing={2}>
              <Grid item xs={12} md={6}>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                  <Button
                      startIcon={<TouchAppIcon/>}
                      onClick={() => handleOpenSelectMultiplePopup (currentPositions)}>
                    Chọn vị trí công tác
                  </Button>
                </ButtonGroup>
              </Grid>

              <Grid item xs={12}>
                <GlobitsTable data={currentPositions} columns={columns} nonePagination selection={false}/>
              </Grid>
            </Grid>
          </TabAccordion>
        </Grid>
        <Grid item xs={12}>
          <StaffWorkingHistoryV2/>
        </Grid>

        {openCreateEditPopup && (
            <PositionCUForm
                isDisabled={!isAdmin}
                staff={selectedStaff}
                handleAfterSubmit={async () => {
                  await fetchPositions (id);
                  await pagingStaffWorkingHistory ({staffId:id});
                }}
            />
        )}

        {openConfirmRemoveFromPosPopup && (
            <GlobitsConfirmationDialog
                open={openConfirmRemoveFromPosPopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmRemove}
                title={t ("confirm_dialog.delete.title")}
                text={"Bạn có chắc muốn bãi nhiệm nhân viên?"}
                agree={t ("confirm_dialog.delete.agree")}
                cancel={t ("confirm_dialog.delete.cancel")}
            />
        )}

        {openConfirmAssignPopup && (
            <GlobitsConfirmationDialog
                open={openConfirmAssignPopup}
                onConfirmDialogClose={handleCloseConfirmAssignPopup}
                onYesClick={handleConfirmAssign}
                title={t ("confirm_dialog.delete.title")}
                text={
                  "Bạn có chắc muốn gán nhân viên vào các vị trí đã chọn? Các nhân viên hiện đang nắm giữ vị trí sẽ bị thay thế bằng nhân viên hiện tại?"
                }
                agree={t ("confirm_dialog.delete.agree")}
                cancel={t ("confirm_dialog.delete.cancel")}
            />
        )}

        {openSelectMultiplePopup && <SelectMulPositionsPopup/>}
      </Grid>
  );
}

export default memo (observer (StaffPositionsV2));
