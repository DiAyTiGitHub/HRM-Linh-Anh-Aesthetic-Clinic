import React from "react";
import { Button, Icon, IconButton, } from "@material-ui/core";
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

export default observer(function StaffPopup({ startIcon, classButton, textButton }) {
  const { t } = useTranslation();
  const { values, setFieldValue } = useFormikContext();

  const [openStaff, setOpenStaff] = useState(false);
  const [listStaff, setListStaff] = useState([]);
  const [listStaffBefore, setListStaffBefore] = useState([]);
  const [lastPage, setLastPage] = useState(false);
  const [pageIndex, setPageIndex] = useState(0);
  const [keyword, setKeyword] = React.useState("");

  useEffect(() => {
    if (openStaff) {
      if (values.project) {
        getProject(values.project.id).then(({ data }) => {
          setListStaff(!data.projectStaff ? listStaff : data.projectStaff);
          setListStaffBefore(!data.projectStaff ? listStaff : data.projectStaff)
          setLastPage(true);
          setPageIndex(0)
        })
      } else {
        if (pageIndex === 0) {
          getPagingStaff(pageIndex + 1)
        }
      }
    }
  }, [openStaff]);

  function getPagingStaff(page, keyword) {
    pagingStaff({ pageIndex: page, pageSize: 10, keyword }).then(({ data }) => {
      let newListStaff = listStaff
      if (pageIndex === 1) {
        newListStaff = data.content;
      } else {
        newListStaff = [...newListStaff, ...data.content]
      }

      if (data.last) {
        setListStaffBefore(newListStaff)
      }

      setKeyword(keyword)
      setPageIndex(page)
      setLastPage(data.last);
      setListStaff(newListStaff);
    })
  }

  function handleChangeStaff(item, check) {
    // let newStaff = values.staffs;
    // if (check) {
    //   newStaff = newStaff.filter(e => e.id !== item.id);
    // } else {
    //   newStaff.push(item);
    // }
    // setFieldValue('staffs', newStaff);

    // one task is assigned for only one staff, so dinhtuandat rewrite this logic
    let newStaff = values.staffs;
    if (check) {
      newStaff = [];
    } else {
      newStaff = [item];
    }
    setFieldValue('staffs', newStaff);
  }

  function onChangeListCurrent(event) {
    if (event.target.value) {
      let result = searchListBefore(listStaffBefore, event.target.value);
      setListStaff(result);
    } else {
      setListStaff(listStaffBefore);
    }
  }

  function searchListBefore(array, matchingName) {
    let resultArr = [];
    
    array.forEach((node) => {
      if (ToAlphabet(node?.displayName).includes(ToAlphabet(matchingName))) {
        resultArr.push(node);
        return resultArr;
      }
    });
    return resultArr;
  };

  return (
    <> 
      <Button
        variant="contained"
        startIcon={startIcon}
        onClick={(event) => setOpenStaff(event.currentTarget)}
        className={classButton}
      >
        {textButton}
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
            <p style={{ textAlign: "center", margin: 0 }}>{t("task.members")}</p>
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
            {values.project ? (
              <GlobitsTextField name="searchStaff" value={keyword} onChange={onChangeListCurrent} />
            ) : (
              <GlobitsTextField
                name="searchStaff"
                onChange={(e) => setKeyword(e.target.value)}
                onKeyPress={(e) => {
                  if (e.key === "Enter") {
                    getPagingStaff(pageIndex, keyword)
                  }
                }}
              />
            )}

          </div>
          <div style={{ maxHeight: '250px', overflow: 'auto', minWidth: 250 }}>
            {listStaff?.map((item, index) => {
              const check = Array.isArray(values?.staffs) ? values?.staffs?.some(e => e?.id === item?.id) : false;

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
              <Button onClick={() => getPagingStaff(pageIndex + 1, keyword)}>Thêm</Button>
            )}
          </div>
        </div>
      </Popover>
    </>
  );
})