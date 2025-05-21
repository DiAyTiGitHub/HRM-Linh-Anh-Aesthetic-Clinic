import React, { memo } from "react";
import { Button, Icon, IconButton, Tooltip, } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import "../_task.scss";
import { useState } from "react";
import { useFormikContext } from "formik";
import { useEffect } from "react";
import { pagingProjectActivity } from "app/views/Project/ProjectService";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import Popover from '@material-ui/core/Popover';
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import ProjectActivityInTaskInfo from "../ProjectActivity/ProjectActivityInTaskInfo";

function ChooseActivityPopup({ startIcon, classButton, textButton }) {
  const { t } = useTranslation();
  const { values, setFieldValue } = useFormikContext();

  const [openActivity, setOpenActivity] = useState(false);
  const [listActivity, setListActivity] = useState([]);
  const [lastPage, setLastPage] = useState(false);
  const [pageIndex, setPageIndex] = useState(1);
  const [keyword, setKeyword] = React.useState("");

  useEffect(() => {
    if (openActivity) {
      //new logic to render initial list activitys in project
      getPagingActivity();
    }
  }, [openActivity, keyword, pageIndex]);

  async function getPagingActivity() {

    let projectId = values?.project?.id;
    //handle for paging all tasks of all projects or only task not in any project
    if (values?.project?.id?.includes('all-project') || values?.project?.id?.includes('none-project')) {
      projectId = null;
    }

    const searchObject = {
      pageIndex,
      pageSize: 10,
      keyword,
      projectId
    };

    const { data } = await pagingProjectActivity(searchObject);
    let newListActivity = listActivity;

    if (pageIndex === 1) {
      newListActivity = data?.content;
    } else {
      newListActivity = [...newListActivity, ...data?.content];
    }

    setLastPage(data?.last);
    setListActivity(newListActivity);
  }

  function handleChangeActivity(item, check) {
    let newActivity = values?.activity;
    if (check) {
      newActivity = null;
    } else {
      newActivity = item;
    }
    setFieldValue('activity', newActivity);

    setOpenActivity(null);
  }

  function handleChangeKeyword(event) {
    setKeyword(event?.target?.value || "");
    setPageIndex(1);
  }

  function handleLoadMoreActivity() {
    setPageIndex(pageIndex + 1);
  }

  const [openListActivity, setOpenListActivity] = useState(false);

  return (
    <>
      <Button
        variant="contained"
        startIcon={startIcon}
        className={`${classButton}`}
        // onClick={(event) => setOpenActivity(event?.currentTarget)}

        // revert old version of choosing activity for task
        onClick={() => setOpenListActivity(true)}

      >
        {textButton}{": "}<b>{values?.activity?.name || "Chưa được giao"}</b>
      </Button>

      {/* <Popover
        anchorEl={openActivity}
        onClose={() => setOpenActivity(null)}
        id={`activity-${classButton}`}
        open={Boolean(openActivity)}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'center',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'center',
        }}
      >
        <div style={{ padding: 10 }}>
          <div style={{ position: 'relative', borderBottom: "1px solid #dee2e6", paddingBottom: 5 }}>
            <p style={{ textAlign: "center", margin: 0 }}>{t("task.acitivity")}</p>
            <IconButton
              style={{ position: "absolute", right: "0", top: "0", padding: 0 }}
              onClick={() => setOpenActivity(null)}
            >
              <Icon color="disabled" title={"Đóng"} fontSize="small" >
                close
              </Icon>
            </IconButton>
          </div>
          <div className="Search" style={{ alignItems: "unset" }}>
            <GlobitsTextField name="searchActivity" value={keyword} onChange={handleChangeKeyword} />

          </div>
          <div style={{ maxHeight: '250px', overflow: 'auto', minWidth: 250 }}>
            {listActivity?.map((item, index) => {
              const check = values?.activity?.id == item?.id;

              return (
                <p
                  key={index}
                  className="Member d-flex flex-middle" style={{ cursor: 'pointer' }}
                  onClick={() => handleChangeActivity(item, check)}
                >
                  {item?.name} {check && <CheckIcon style={{ height: '15px' }} color='primary' />}
                </p>
              )
            })}

            {!lastPage && (
              <Tooltip title="Tải thêm dữ liệu" placement="bottom">
                <button
                  type="button"
                  onClick={handleLoadMoreActivity}
                  className="w-100 loadMoreStaffBtn"
                >
                  <MoreHorizIcon className="mr-2" />
                  More
                </button>
              </Tooltip>
            )}
          </div>
        </div>
      </Popover> */}

      {openListActivity && (
        <ProjectActivityInTaskInfo
          open={openListActivity}
          handleClose={() => setOpenListActivity(false)}
          setIsOpenListActivity={setOpenListActivity}
        />
      )}

    </>
  );
}

export default memo(observer(ChooseActivityPopup));