import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import { useFormikContext } from "formik";
import { useStore } from "app/stores";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";
import { pagingProject } from "app/views/Project/ProjectService";
import { toast } from "react-toastify";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

function FilterActivitySection(props) {
    const { isOpenFilter } = props;

    const {
        taskStore,
        projectActivityStore
    } = useStore();

    const {
        getOnViewProjects,
        getKanbanFilter
    } = taskStore;

    const {
        getListActivityById,
        listActivities,
    } = projectActivityStore;

    const { values, setFieldValue } = useFormikContext();

    const [projectOptions, setProjectOptions] = useState([]);

    //load project to choose in filter
    useEffect(function () {
        // setFieldValue("project", null);

        if (isOpenFilter) {
            //default choosing project when only 1 project can choosen
            const canViewProjects = getOnViewProjects();
            if (canViewProjects && canViewProjects?.length == 1) {
                //confirm this is not option all-project or none-project
                if (!canViewProjects[0]?.id?.includes('all-project') && !canViewProjects[0]?.id?.includes('none-project')) {
                    setFieldValue("project", canViewProjects[0]);
                    setProjectOptions(getOnViewProjects());
                }
                else if (canViewProjects[0]?.id?.includes('all-project')) {
                    //when its is all project, load all option project user can view to choosen
                    const soProject = {
                        keyword: values?.keyword
                    };

                    const getProject = async () => {
                        var searchObject = {
                            pageIndex: 1,
                            pageSize: 1000,
                        };

                        try {
                            let data = await pagingProject(searchObject);
                            setProjectOptions(data?.data?.content);
                        } catch (error) {
                            toast.error("Có lỗi xảy ra khi lấy dự liệu dự án để chọn trong bộ lọc");
                        }
                    };

                    getProject(soProject);
                }
                else {
                    //when chosen to see none project => option is empty
                }
            }
            //when user selected multiple projects to view, only can choose projects in that list selected
            else {
                setProjectOptions(canViewProjects);

            }
        }
    }, [isOpenFilter]);


    //handle change options project activity when project is changed
    useEffect(function () {
        async function updateActivityOption() {

            //only set projectActivity to null if field project in kanbanfilter is different from current chosen project
            const kanbanFilter = getKanbanFilter();
            if (kanbanFilter?.project?.id != values?.project?.id) {
                setFieldValue("projectActivity", null);
            }

            if (values?.project && values?.project?.id) {
                //call api to render list activity of project            
                await getListActivityById({ projectId: values?.project?.id, includeAll: false });
            }
        }

        updateActivityOption();
    }, [values?.project?.id]);

    function hanleChangeCheckbox({ target }) {
        setFieldValue("includeChildrenActivities", target?.checked);
    }

    // console.log("changed values: ", values);

    return (
        <>
            <Grid item xs={12} className="pb-0">
                <p className="m-0 p-0 borderThrough2">
                    Hoạt động dự án
                </p>
            </Grid>

            <Grid item xs={12} sm={6} md={4} lg={3}>
                <GlobitsAutocomplete
                    name="project"
                    className="w-100"
                    options={projectOptions}
                    label="Dự án"
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4} lg={3}>
                <GlobitsAutocomplete
                    name="projectActivity"
                    className="w-100"
                    options={listActivities}
                    label="Hoạt động"
                />

            </Grid>


            <Grid item xs={12} sm={6} md={4} lg={3} className="align-center flex pt-24">
                <input
                    type="checkbox"
                    name="includeChildrenActivities"
                    checked={values?.includeChildrenActivities}
                    onChange={hanleChangeCheckbox}
                />
                <p className="pl-8 filterField">
                    Tìm kiếm trong cả các hoạt động con
                </p>
            </Grid>
        </>
    );

}

export default memo(observer(FilterActivitySection));