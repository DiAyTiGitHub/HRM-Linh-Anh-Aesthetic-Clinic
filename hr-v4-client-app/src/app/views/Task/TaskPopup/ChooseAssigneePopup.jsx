import React, { memo } from "react";
import { Button, Icon, IconButton, Tooltip, } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import "../_task.scss";
import { useState } from "react";
import { useFormikContext } from "formik";
import { useEffect } from "react";
import { getProject } from "app/views/Project/ProjectService";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { ToAlphabet } from "app/LocalFunction";
import Popover from '@material-ui/core/Popover';
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";

function ChooseAssigneePopup({ startIcon, classButton, textButton }) {
  const { t } = useTranslation();
  const { values, setFieldValue } = useFormikContext();

  const [openStaff, setOpenStaff] = useState(false);
  const [listStaff, setListStaff] = useState([]);
  const [lastPage, setLastPage] = useState(false);
  const [pageIndex, setPageIndex] = useState(1);
  const [keyword, setKeyword] = React.useState("");

  useEffect(() => {
    if (openStaff) {
      //new logic to render initial list staffs in project
      getPagingStaff();
    }
  }, [openStaff, keyword, pageIndex]);

  async function getPagingStaff() {
    //is including searching for all staff existed in project when this task is edited
    let includeVoidedInProject = false;
    //if this task is new, only on joining staffs are available to choose
    if (!values?.id) includeVoidedInProject = false;

    let projectId = values?.project?.id;
    //handle for paging all tasks of all projects or only task not in any project
    if (values?.project?.id?.includes('all-project') || values?.project?.id?.includes('none-project')) {
      projectId = null;
    }

    const searchObject = {
      pageIndex,
      pageSize: 10,
      keyword,
      includeVoidedInProject,
      projectId
    };

    const { data } = await pagingStaff(searchObject);
    let newListStaff = listStaff;

    if (pageIndex === 1) {
      newListStaff = data?.content;
    } else {
      newListStaff = [...newListStaff, ...data?.content]
    }

    setLastPage(data?.last);
    setListStaff(newListStaff);
  }

  function handleChangeStaff(item, check) {
    let newStaff = values?.assignee;
    if (check) {
      newStaff = null;
    } else {
      newStaff = item;
    }
    setFieldValue('assignee', newStaff);

    setOpenStaff(null);
  }

  function handleChangeKeyword(event) {
    setKeyword(event?.target?.value || "");
    setPageIndex(1);
  }

  function handleLoadMoreStaff() {
    setPageIndex(pageIndex + 1);
  }

  return (
    <>
      <Button
        variant="contained"
        startIcon={startIcon}
        onClick={(event) => setOpenStaff(event?.currentTarget)}
        className={`${classButton}`}
      >
        {textButton}{": "}<b>{values?.assignee?.displayName || "Chưa được giao"}</b>
      </Button>

      <Popover
        anchorEl={openStaff}
        onClose={() => setOpenStaff(null)}
        id={`staff-${classButton}`}
        open={Boolean(openStaff)}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'center',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'center',
        }}
      >
        <div className="p-8">
          <div style={{ position: 'relative', borderBottom: "1px solid #dee2e6", paddingBottom: 5 }}>
            <p className="text-center m-0">Người phụ trách</p>
            <IconButton
              style={{ position: "absolute", right: "0", top: "0", padding: 0 }}
              onClick={() => setOpenStaff(null)}
            >
              <Icon color="disabled" title={"Đóng"} fontSize="small" >
                close
              </Icon>
            </IconButton>
          </div>
          <div className="Search" style={{ alignItems: "unset" }}>
            <GlobitsTextField
              name="searchStaff"
              value={keyword}
              onChange={handleChangeKeyword}
              placeholder="Tìm kiếm..."
            />
          </div>
          <div
            style={{ maxHeight: '250px', overflow: 'auto', minWidth: 250 }}
            className="styledThinScrollbar pt-4"
          >
            {listStaff?.map((item, index) => {
              const check = values?.assignee?.id == item?.id;

              return (
                <p
                  key={index}
                  className="Member d-flex flex-middle" style={{ cursor: 'pointer' }}
                  onClick={() => handleChangeStaff(item, check)}
                >
                  {item?.displayName} {check && <CheckIcon style={{ height: '15px' }} color='primary' />}
                </p>
              )
            })}

            {!lastPage && (
              <Tooltip title="Tải thêm dữ liệu" placement="bottom">
                <button
                  type="button"
                  onClick={handleLoadMoreStaff}
                  className="w-100 loadMoreStaffBtn"
                >
                  <MoreHorizIcon className="mr-2" />
                  More
                </button>
              </Tooltip>
            )}
          </div>
        </div>
      </Popover>
    </>
  );
}

export default memo(observer(ChooseAssigneePopup));