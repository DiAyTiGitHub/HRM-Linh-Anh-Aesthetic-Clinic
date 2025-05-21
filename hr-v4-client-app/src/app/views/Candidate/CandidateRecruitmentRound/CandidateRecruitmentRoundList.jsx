import React, { memo, useMemo, useState } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip, MenuItem, Menu } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import LocalConstants from "app/LocalConstants";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import ConstantList from "app/appConfig";
import { useHistory } from "react-router-dom";

function CandidateRecruitmentRoundList() {
  const { 
    candidateRecruitmentRoundStore,
    recruitmentStore
   } = useStore();
  const { t } = useTranslation();
  const history = useHistory();

  const {
    listCandidateRecruitmentRound,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,

    handleOpenMoveToNextRoundPopup,
    handleOpenUpdateResultPopup,

    isLastRecruitmentRound
  } = candidateRecruitmentRoundStore;

  const {
    selectedRecruitment
  } = recruitmentStore;

  const [anchorEl, setAnchorEl] = useState();
  const [selectedRow, setSelectedRow] = useState(null);

  function handleClosePopover() {
    setAnchorEl(null);
    setSelectedRow(null);
  }

  const columns = [
    {
      title: t("general.action"),
      minWidth: "100px",
      render: (rowData) => {

        return (
          <div className="flex flex-middle justify-center">
            <Tooltip title="Hồ sơ ứng viên" placement="top">
              <IconButton size="small" onClick={function () {
                //link to new page like a staff
                history.push(ConstantList.ROOT_PATH + `candidate/` + rowData?.candidate?.id + "?isFromCandidateRecruitmentRound=true");
              }}>
                <Icon fontSize="small" color="primary">
                  remove_red_eye
                </Icon>
              </IconButton>
            </Tooltip>

            <Tooltip title="Cập nhật kết quả" placement="top">
              <IconButton className="ml-4" size="small" onClick={() => handleOpenCreateEdit(rowData?.id)}>
                <Icon fontSize="small" color="secondary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>

            <Tooltip title="Xóa ứng viên khỏi vòng tuyển" placement="top">
              <IconButton className="ml-4" size="small" onClick={() => handleDelete(rowData)}>
                <Icon fontSize="small" color="secondary">
                  delete
                </Icon>
              </IconButton>
            </Tooltip>

            <Tooltip title="Thao tác khác" placement="top">
              <IconButton
                className="ml-4"
                size="small"
                onClick={(event) => {
                  setSelectedRow(rowData);
                  setAnchorEl(event?.currentTarget);
                }}
              >
                <MoreHorizIcon />
              </IconButton>
            </Tooltip>
          </div >
        );

      }
    },
    {
      title: "Mã ứng viên",
      field: "candidate.candidateCode",
    },
    {
      title: "Họ tên",
      field: "candidate.displayName",
    },
    {
      title: "Ngày sinh",
      field: "candidate.birthDate",
      render: (rowData) => (
        <span>
          {rowData?.candidate?.birthDate && (formatDate("DD/MM/YYYY", rowData?.candidate.birthDate))}
        </span>
      ),
    },
    {
      title: "Giới tính",
      field: "candidate.gender",
      render: rowData => (
        <span>
          {rowData?.candidate?.gender === "M" ? "Nam" : rowData?.candidate?.gender === "F" ? "Nữ" : ""}
        </span>
      )
    },
    {
      title: "SĐT",
      field: "candidate.phoneNumber",
      render: rowData => (
        <>
          {rowData?.phoneNumber && (
            <span className="pr-8">
              {rowData?.candidate?.phoneNumber}
            </span>
          )}
        </>
      )
    },
    {
      title: "Ngày nộp hồ sơ",
      field: "candidate.submissionDate",
      render: (rowData) => (
        <span>
          {rowData?.candidate?.submissionDate && (formatDate("DD/MM/YYYY", rowData?.candidate?.submissionDate))}
        </span>
      ),
    },

    {
      title: "Vị trí dự thi",
      field: "examPosition",
      render: (rowData) => (
        <span>
          {rowData?.examPosition}
        </span>
      ),
    },
    {
      title: "Thời gian dự thi",
      field: "actualTakePlaceDate",
      render: (rowData) => (
        <span>
          {rowData?.actualTakePlaceDate && (formatDate("HH:mm DD/MM/YYYY", rowData?.actualTakePlaceDate))}
        </span>
      ),
    },
    {
      title: "Kết quả",
      field: "result",
      render: (rowData) => {
        // console.log("rowData: ", rowData);

        return (
          <span>
            {rowData?.result == 0 ? "Chưa có kết quả" : (LocalConstants.CandidateExamStatus.getValueByKey(rowData?.result))}
          </span>
        );
      },
    },
  ];

  return (
    <>
      <GlobitsTable
        selection
        data={listCandidateRecruitmentRound}
        handleSelectList={handleSelectListDelete}
        columns={columns}
        totalPages={totalPages}
        handleChangePage={handleChangePage}
        setRowsPerPage={setPageSize}
        pageSize={searchObject?.pageSize}
        pageSizeOption={[5, 10, 25, 50, 100]}
        totalElements={totalElements}
        page={searchObject?.pageIndex}
      />

      {Boolean(anchorEl) && (
        <Menu
          id={"simple-menu-options"}
          anchorEl={anchorEl}
          keepMounted
          open={Boolean(anchorEl)}
          onClose={handleClosePopover}
          className="py-0"
        >
          <MenuItem className="flex items-center justify-center" onClick={function () {
            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
            handleOpenUpdateResultPopup();
            handleClosePopover();
          }}>
            <Icon className="pr-6" fontSize="small">
              hourglass_full
            </Icon>

            <span className="ml-4">
              Cập nhật kết quả
            </span>
          </MenuItem>

          <MenuItem className="flex items-center justify-center" onClick={function () {
            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
            handleOpenMoveToNextRoundPopup();
            handleClosePopover();
          }}>
            <Icon className="pr-6" fontSize="small" style={{ color: "green" }}>
              queue_play_next
            </Icon>

            <span className="ml-4">
              {isLastRecruitmentRound(selectedRecruitment) ? "Đánh dấu đã ĐẠT kì thi tuyển" : "Chuyển đến vòng thi tiếp"}
            </span>
          </MenuItem>
        </Menu>
      )}
    </>

  );
}

export default memo(observer(CandidateRecruitmentRoundList));
