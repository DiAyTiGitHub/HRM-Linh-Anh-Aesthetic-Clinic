/* eslint-disable react-hooks/exhaustive-deps */
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { ToAlphabet } from "app/LocalFunction";
import { useStore } from "app/stores";
import { Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { toast } from "react-toastify";
import { pagingTreeDepartments } from "../Department/DepartmentService";
import TreeViewDepartment from "../HumanResourcesInformation/Component/TreeViewDepartment";
import PositionCUForm from "../Position/PositionCUForm";
import PositionList from "../Position/PositionList";
import PositionToolbar from "../Position/PositionToolbar";
import DepartmentToolbar from "./components/Toolbox/DepartmentToolbar";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import PositionTransferForm from "../Position/PositionTransferForm";
import StaffTransferForm from "../Position/StaffTransferForm";
import DepartmentV2CUForm from "../DepartmentV2/DepartmentV2CUForm";
import { OrganizationType } from "../../LocalConstants";

const initialValues = {
  page:1,
  rowsPerPage:10,
  keyword:null,
  organization:{
    organizationType:OrganizationType.OPERATION.value,
  },
};

function OrganizationTreeIndex (props) {
  const {t} = useTranslation ();
  let query = useLocation ();

  const [departmentTreeView, setDepartmentTreeView] = useState ([]);
  const [filterDepartmentTreeView, setFilterDepartmentTreeView] = useState ([]);
  const [selectedDepartment, setSelectedDepartment] = useState (null);
  const [toolbarValues, setToolbarValues] = useState ({}); // Thêm state để lưu trữ giá trị của DepartmentToolbar

  const {
    openCreateEditPopup:openCreateEditPopupDepartment,
    openViewPopup:openViewPopupDepartment
  } = useStore ().departmentV2Store;

  const {
    searchObject,
    handleSetSearchObject,
    pagingPosition,
    openCreateEditPopup,
    openViewPopup,
    handleClose,
    handleConfirmDelete,
    resetStore,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    handleConfirmDeleteList,
  } = useStore ().positionStore;

  const [searchTreeObject, setSearchTreeObject] = useState ();

  const handleSearchDepartment = (event) => {
    setSearchTreeObject (event.target.value);
    if (event.target.value) {
      let result = searchTree (departmentTreeView, event.target.value);
      setFilterDepartmentTreeView (result);
    } else {
      setFilterDepartmentTreeView (departmentTreeView || []);
    }
  };

  const searchTree = (array, matchingName) => {
    let resultArr = [];
    array.forEach ((node) => {
      if (ToAlphabet (node.name).includes (ToAlphabet (matchingName))) {
        resultArr.push (node);
        return resultArr;
      } else if (node.children != null) {
        let subArr = searchTree (node.children, matchingName);
        resultArr.push (... subArr);
      }
    });
    return resultArr;
  };

  const handleSearchTreeDepartment = (array, matchingName) => {
    let resultArr = [];
    if (matchingName) {
      array.forEach ((node) => {
        if (ToAlphabet (node.name).includes (ToAlphabet (matchingName))) {
          resultArr.push (node);
          return resultArr;
        } else if (node.children != null) {
          let subArr = searchTree (node.children, matchingName);
          resultArr.push (... subArr);
        }
      });
    } else {
      resultArr = [... array];
    }
    return resultArr;
  };

  const handleAfterSubmit = () => {
    if (selectedOrg) {
      handleFilter (selectedOrg);
    }
  };

  const handleFilter = (org) => {
    let dto = {pageIndex:1, pageSize:1000};
    if (org) {
      dto.organizationId = org.id;
    }

    pagingTreeDepartments (dto)
        .then (({data}) => {
          const {content} = data;
          setDepartmentTreeView (content);
          setFilterDepartmentTreeView (handleSearchTreeDepartment (content, searchTreeObject));
        })
        .catch ((error) => {
          console.error (error);
          toast.error (t ("Có lỗi xảy ra trong quá trình tìm kiếm"));
        });
  };

  const handleAfterSubmitPosition = useCallback ((position) => {
    console.log (position);
    let newDepartmentTree = departmentTreeView?.map ((d) => {
      if (d?.id === position?.department?.id) {
        return {... d, numberOfPositions:d?.numberOfPositions + 1};
      } else {
        return {... d};
      }
    });
    setDepartmentTreeView (newDepartmentTree);
    setFilterDepartmentTreeView (handleSearchTreeDepartment (newDepartmentTree, searchTreeObject));
  }, [departmentTreeView, searchTreeObject]);

  const handleAfterChooseDepartment = (department) => {
    setSelectedDepartment (department)
  }

  const [selectedOrg, setselectedOrg] = useState ({});

  const {isAdmin, checkAllUserRoles} = useStore ().hrRoleUtilsStore;
  useEffect (() => {
    checkAllUserRoles ()
  }, [])
  return (
      <Formik enableReinitialize initialValues={initialValues}>
        {({resetForm, values, setFieldValue, setValues}) => {
          return (
              <section className="staff-root flex-column">
                <div className='px-25 bg-white'>
                  <GlobitsBreadcrumb
                      routeSegments={[{name:t ("Cơ cấu tổ chức")}, {name:t ("Cây tổ chức")}]}
                  />
                </div>


                <Grid container spacing={2} className='p-12 h-100 pb-48'>
                  <Grid item xs={4}>
                    <Grid container spacing={2}>
                      <Grid item xs={12}>
                        {/* Filter */}
                        <DepartmentToolbar
                            handleFilter={handleFilter}
                            setselectedOrg={setselectedOrg}
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <TreeViewDepartment
                            onNodeSelect={(event, departmentId) => {
                              handleSetSearchObject ({
                                ... searchObject,
                                pageIndex:1,
                                department:{id:departmentId},
                              });
                              setSelectedDepartment (departmentId);
                              pagingPosition ();
                            }}
                            departmentTreeView={filterDepartmentTreeView}
                            handleSearch={handleSearchDepartment}
                            selectedNode={selectedDepartment}
                        />
                      </Grid>
                    </Grid>
                  </Grid>
                  <Grid item xs={8} className='h-100'>
                    <Grid container spacing={2}>
                      <Grid item xs={12}>
                        <PositionToolbar/>
                      </Grid>

                      <Grid item xs={12}>
                        <PositionList/>
                      </Grid>
                      <PositionTransferForm/>
                      <StaffTransferForm/>
                    </Grid>
                  </Grid>
                </Grid>

                {openCreateEditPopup && <PositionCUForm handleAfterSubmit={handleAfterSubmitPosition}/>}
                {openViewPopup && <PositionCUForm readOnly={true}/>}

                {openCreateEditPopupDepartment &&
                    <DepartmentV2CUForm/>}

                {openViewPopupDepartment && (
                    <DepartmentV2CUForm readOnly={true}/>
                )}

                {openConfirmDeletePopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeletePopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDelete}
                        title={t ("confirm_dialog.delete.title")}
                        text={t ("confirm_dialog.delete.text")}
                        agree={t ("confirm_dialog.delete.agree")}
                        cancel={t ("confirm_dialog.delete.cancel")}
                    />
                )}

                {openConfirmDeleteListPopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeleteListPopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDeleteList}
                        title={t ("confirm_dialog.delete_list.title")}
                        text={t ("confirm_dialog.delete_list.text")}
                        agree={t ("confirm_dialog.delete_list.agree")}
                        cancel={t ("confirm_dialog.delete_list.cancel")}
                    />
                )}
              </section>
          );
        }}
      </Formik>
  );
}

export default memo (observer (OrganizationTreeIndex));
