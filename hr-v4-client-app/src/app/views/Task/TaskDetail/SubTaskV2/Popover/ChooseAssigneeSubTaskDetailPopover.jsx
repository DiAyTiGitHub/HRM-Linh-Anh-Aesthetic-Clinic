import React, { memo } from "react";
import { Icon, IconButton, Tooltip, } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useState } from "react";
import { useFormikContext } from "formik";
import { useEffect } from "react";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import Popover from '@material-ui/core/Popover';
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";

function ChooseAssigneeSubTaskDetailPopover(props) {
    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();

    const [listStaff, setListStaff] = useState([]);
    const [lastPage, setLastPage] = useState(false);
    const [pageIndex, setPageIndex] = useState(1);
    const [keyword, setKeyword] = React.useState("");

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

    function handleChangeKeyword(event) {
        setKeyword(event?.target?.value || "");
        setPageIndex(1);
    }

    function handleLoadMoreStaff() {
        setPageIndex(pageIndex + 1);
    }




    const {
        anchorEl,
        detailItemIndex,
        subTaskIndex,
        handleCloseChooseDoer
    } = props;

    useEffect(() => {
        if (Boolean(anchorEl)) {
            //new logic to render initial list staffs in project
            getPagingStaff();
        }
    }, [Boolean(anchorEl), keyword, pageIndex]);

    function handleChooseStaff(item, check) {
        const oldStaffList = values?.subTasks[subTaskIndex]?.items[detailItemIndex]?.staffs;
        let newStaff = null;
        if (oldStaffList && oldStaffList?.length > 0) newStaff = oldStaffList[0];

        if (check) {
            newStaff = [];
        } else {
            newStaff = [item];
        }

        setFieldValue(`subTasks[${subTaskIndex}].items[${detailItemIndex}].staffs`, newStaff);
        handleCloseChooseDoer();
    }

    return (
        <Popover
            anchorEl={anchorEl}
            onClose={handleCloseChooseDoer}
            // id={`staff-${subTaskIndex}-${detailItemIndex}`}
            id={'simple-popove'}
            open={Boolean(anchorEl)}
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
                        onClick={handleCloseChooseDoer}
                    >
                        <Icon color="disabled" title={"Đóng"} fontSize="small" >
                            close
                        </Icon>
                    </IconButton>
                </div>
                <div className="Search" style={{ alignItems: "unset" }}>
                    <GlobitsTextField
                        placeholder="Tìm kiếm..."
                        name="searchStaff"
                        value={keyword}
                        onChange={handleChangeKeyword}
                    />
                </div>
                <div
                    style={{ maxHeight: '250px', overflow: 'auto', minWidth: 250 }}
                    className="styledThinScrollbar mt-2"
                >
                    {listStaff?.map((staff, index) => {
                        let check = false;
                        if (values?.subTasks[subTaskIndex]?.items[detailItemIndex]
                            && values?.subTasks[subTaskIndex]?.items[detailItemIndex]?.staffs
                            && values?.subTasks[subTaskIndex]?.items[detailItemIndex]?.staffs?.length > 0
                            && values?.subTasks[subTaskIndex]?.items[detailItemIndex]?.staffs[0]?.id == staff?.id)
                            check = true;

                        return (
                            <p
                                key={index}
                                className="Member d-flex flex-middle" style={{ cursor: 'pointer' }}
                                onClick={() => handleChooseStaff(staff, check)}
                            >
                                {staff?.displayName} {check && <CheckIcon style={{ height: '15px' }} color='primary' />}
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
    );
}

export default memo(observer(ChooseAssigneeSubTaskDetailPopover));