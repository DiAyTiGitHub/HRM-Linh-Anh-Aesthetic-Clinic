import React, { memo } from "react";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import { useStore } from "app/stores";
import CheckIcon from '@material-ui/icons/Check';
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import Popover from '@material-ui/core/Popover';
import { Button } from '@material-ui/core';
import CloseIcon from '@material-ui/icons/Close';

function ProjectChip(props) {
    const {
        project,

    } = props;

    const { values, setFieldValue } = useFormikContext();
    const { taskStore } = useStore();
    const { handleSaveViewingProjects } = taskStore;

    async function handleDeleteViewingProject() {
        const onSaveProjects = values?.projectList;

        let deleteIndex = -1;
        for (let i = 0; i < onSaveProjects?.length; i++) {
            if (onSaveProjects[i]?.id == project?.id) {
                deleteIndex = i;
                break;
            }
        }
        if (deleteIndex != -1) {
            onSaveProjects.splice(deleteIndex, 1);
        }

        // console.log("checking onsave project: ", onSaveProjects);
        //immediately rerender can view tasks
        await handleSaveViewingProjects(onSaveProjects);

        setFieldValue("projectList", onSaveProjects);
    }

    return (
        <div className="projectChip flex flex-center">
            <div className="m-0 projectChipName text-white">
                {project?.name}
            </div>
            <Tooltip placement="top" title="Bỏ chọn dự án">
                <CloseIcon
                    onClick={handleDeleteViewingProject}
                    className="text-white ml-4 projectChipIcon"
                    style={{ fontSize: "20px" }}
                />
            </Tooltip>

        </div>
    );
}

export default memo(observer(ProjectChip));