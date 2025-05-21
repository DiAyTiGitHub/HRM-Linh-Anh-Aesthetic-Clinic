import React, { memo, useState, useEffect } from "react";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import { useStore } from "app/stores";
import CheckIcon from '@material-ui/icons/Check';
import { observer } from "mobx-react";
import "../../_task.scss";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import Popover from '@material-ui/core/Popover';
import { Button, Grid } from '@material-ui/core';
import ProjectChip from "./ProjectChip";
import ClearAllIcon from '@material-ui/icons/ClearAll';

function ChooseMultipleProjectsPopover(props) {
    const {

    } = props;

    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext();

    const { taskStore } = useStore();

    const {
        availableProjectList,
        getOnViewProjects,
        handleSaveViewingProjects,
        handleRenderChosenProjectNames,
        getAllProjectsForChooseMultiplePopover,
    } = taskStore;

    const [openPopover, setOpenPopover] = useState(false);

    // THIS FUNCTION is USED FOR HANDLING CHOOSING MULTIPLE PROJECTS TO VIEW
    function handleChangeListChosenProjects(onChooseProject) {
        let chosenProjects = values?.projectList;

        //handle for toggle "all-project" option
        if (onChooseProject?.id?.includes('all-project')) {
            let existingIndex = -1;
            for (let i = 0; i < chosenProjects?.length; i++) {
                const index = i;
                const chosenProject = chosenProjects[i];

                if (chosenProject?.id?.includes('all-project')) {
                    existingIndex = index;
                    break;
                }
            }

            if (existingIndex == -1) {
                setFieldValue("projectList", [onChooseProject]);
            }
            else {
                setFieldValue("projectList", []);
            }
        }

        //handle for toggle "none-project" option
        else if (onChooseProject?.id?.includes('none-project')) {
            let existingIndex = -1;
            for (let i = 0; i < chosenProjects?.length; i++) {
                const index = i;
                const chosenProject = chosenProjects[i];

                if (chosenProject?.id?.includes('none-project')) {
                    existingIndex = index;
                    break;
                }
            }

            if (existingIndex == -1) {
                setFieldValue("projectList", [onChooseProject]);
            }
            else {
                setFieldValue("projectList", []);
            }
        }
        else {
            //toggle chosen project item
            let existingIndex = -1;
            for (let i = 0; i < chosenProjects?.length; i++) {
                const index = i;
                const chosenProject = chosenProjects[i];

                if (chosenProject?.id == onChooseProject?.id) {
                    existingIndex = index;
                    break;
                }
            }

            if (existingIndex == -1) {
                chosenProjects = [...chosenProjects, onChooseProject];
            }
            else {
                chosenProjects.splice(existingIndex, 1);
            }

            //check whether containing used to choose "all-project" option
            existingIndex = -1;
            for (let i = 0; i < chosenProjects?.length; i++) {
                const index = i;
                const chosenProject = chosenProjects[i];

                if (chosenProject?.id?.includes('all-project')) {
                    existingIndex = index;
                    break;
                }
            }
            if (existingIndex != -1) {
                chosenProjects.splice(existingIndex, 1);
            }


            //check whether containing used to choose "none-project" option
            existingIndex = -1;
            for (let i = 0; i < chosenProjects?.length; i++) {
                const index = i;
                const chosenProject = chosenProjects[i];

                if (chosenProject?.id?.includes('none-project')) {
                    existingIndex = index;
                    break;
                }
            }
            if (existingIndex != -1) {
                chosenProjects.splice(existingIndex, 1);
            }


            setFieldValue("projectList", chosenProjects);
        }
    }

    async function handleSubmitListProjects() {
        await handleSaveViewingProjects(values?.projectList);

        setOpenPopover(false);
    }

    function handleClearAllChosenProject() {
        setFieldValue("projectList", []);
    }

    function handleClosePopover() {
        setOpenPopover(null);
        setFieldValue("projectList", getOnViewProjects());
    }

    useEffect(function () {
        if (openPopover) {
            const soProject = {
                keyword: values?.keyword
            };
            getAllProjectsForChooseMultiplePopover(soProject);
        }
    }, [openPopover, values?.keyword]);

    const chosenProjects = getOnViewProjects();

    return (
        <>
            <div className="taskScreenTitleWrapper flex">
                <button className="taskScreenTitle " type="button" onClick={(event) => setOpenPopover(event?.currentTarget)}>
                    <Tooltip placement="top" title="Ấn để chọn các dự án cần hiển thị">
                        <span className="pr-4">
                            Công việc trong dự án: {(!chosenProjects || chosenProjects?.length == 0) && "Chưa chọn dự án"}
                        </span>
                    </Tooltip>
                </button>

                {/* <button type="button" onClick={(event) => setOpenPopover(event?.currentTarget)}>
                    {handleRenderChosenProjectNames()}
                </button> */}

                <div className="projectChipContainer flex-1">
                    {chosenProjects?.map(function (project, index) {

                        return (
                            <ProjectChip
                                project={project}
                                key={index}
                            />
                        );
                    })}
                </div>
            </div>

            <Popover
                anchorEl={openPopover}
                open={Boolean(openPopover)}
                onClose={handleClosePopover}
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
                    <div className="pb-4" style={{ position: 'relative', borderBottom: "1px solid #dee2e6" }}>
                        <p className="flex flex-center m-0">{t('Dự án')}</p>
                        <IconButton
                            className="p-0"
                            style={{ position: "absolute", right: "0", top: "0" }}
                            onClick={handleClosePopover}
                        >
                            <Icon color="disabled" title={"Đóng"} fontSize="small" >
                                close
                            </Icon>
                        </IconButton>
                    </div>

                    <GlobitsTextField name="keyword" />

                    <div className="mt-8 scrollContentPopoverChooseMultipleProject">
                        {availableProjectList?.map((project, index) => {
                            let check = false;

                            for (let i = 0; i < values?.projectList?.length; i++) {
                                const chosenProject = values?.projectList[i];

                                if (project?.id == chosenProject?.id) check = true;
                                else if (project?.id?.includes('all-project') && chosenProject?.id?.includes('all-project')) check = true;
                                else if (project?.id?.includes('none-project') && chosenProject?.id?.includes('none-project')) check = true;

                                if (check) {
                                    break;
                                }
                            }

                            return (
                                <p key={index}
                                    className="Member d-flex flex-middle" style={{ cursor: 'pointer' }}
                                    onClick={() => handleChangeListChosenProjects(project)}
                                >
                                    {project?.name}
                                    {check && <CheckIcon style={{ height: '15px' }} color='primary' />}
                                </p>
                            )
                        })}

                        {(!availableProjectList || availableProjectList.length == 0) && (
                            <p
                                className="flex-center flex m-0 p-8"
                            >
                                Chưa có dự án thỏa mãn
                            </p>
                        )}


                    </div>
                    <Grid className="mt-6" container spacing={1}>
                        <Grid item xs={12} sm={6}>
                            <Button
                                fullWidth
                                className='bg-light-gray'
                                onClick={handleClearAllChosenProject}
                            >
                                <ClearAllIcon className="mr-4" />
                                Bỏ chọn
                            </Button>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <Button
                                fullWidth
                                className='text-white bgc-green-d1'
                                onClick={handleSubmitListProjects}
                            >
                                <CheckIcon className="mr-4" />
                                Lưu lựa chọn
                            </Button>
                        </Grid>
                    </Grid>

                </div>
            </Popover>
        </>
    );
}

export default memo(observer(ChooseMultipleProjectsPopover));