import React, { memo, useState } from "react";
import { Icon, IconButton } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import { useStore } from "app/stores";
import CheckIcon from '@material-ui/icons/Check';
import { observer } from "mobx-react";
import { useEffect } from "react";
import "../_task.scss";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import Popover from '@material-ui/core/Popover';

function ProjectPopupTask({ title = 'Dự án', onChangeProject, itemProjectDefault }) {
    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext();

    const { projectStore } = useStore();
    const {
        availableProjectList,
        pagingProject
    } = projectStore;

    const [openPopover, setOpenPopover] = useState(false);

    useEffect(function () {
        //only paging project when popover is open/reopen
        if (openPopover && Boolean(openPopover)) {
            const searchWrapper = {
                keyword: values?.searchProjectKeyword
            };

            pagingProject(searchWrapper);
        }
    }, [openPopover, values?.searchProjectKeyword]);

    function handleChooseProject(project, isChecked) {
        setFieldValue('project', isChecked ? null : project);
    }

    return (
        <>
            <p className="project-popup-task">
                {title}:

                <button
                    className="pl-4"
                    type="button"
                    onClick={(event) => setOpenPopover(event?.currentTarget)}
                >
                    {values?.project ? values?.project?.name : 'Chưa chọn dự án'}
                </button>
            </p>

            <Popover
                anchorEl={openPopover}
                open={Boolean(openPopover)}
                onClose={() => setOpenPopover(null)}
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
                    <div className="pb-4 mb-8" style={{ position: 'relative', borderBottom: "1px solid #dee2e6" }}>
                        <p className="m-0 text-center">Dự án</p>
                        <IconButton
                            className="p-0"
                            style={{ position: "absolute", right: "0", top: "0" }}
                            onClick={() => setOpenPopover(null)}
                        >
                            <Icon color="disabled" title={"Đóng"} fontSize="small" >
                                close
                            </Icon>
                        </IconButton>
                    </div>

                    <GlobitsTextField name="searchProjectKeyword"
                        placeholder="Tìm kiếm..."
                    />

                    <div
                        style={{ maxHeight: '280px', overflow: 'auto', minWidth: 250 }}
                        className="styledThinScrollbar mt-10"
                    >
                        {availableProjectList?.map((project, index) => {
                            const check = (values?.project) ? values?.project?.id === project?.id : false;

                            return (
                                <p key={index}
                                    className="Member d-flex flex-middle" style={{ cursor: 'pointer' }}
                                    onClick={() => handleChooseProject(project, check)}
                                >
                                    {project?.name}
                                    {check && <CheckIcon style={{ height: '15px' }} color='primary' />}
                                </p>
                            )
                        })}

                        {(!availableProjectList || availableProjectList?.length == 0) && (
                            <p className="p-8 flex flex-center m-0">
                                Không có dự án
                            </p>
                        )}
                    </div>
                </div>
            </Popover>
        </>
    );
}

export default memo(observer(ProjectPopupTask));