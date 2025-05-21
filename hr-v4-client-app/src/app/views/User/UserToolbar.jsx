import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";

function UserToolbar () {
  const {userStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();

  const {
    searchObject,
    handleEditUser,
    handleSetSearchObject,
    search
  } = userStore;

  async function handleFilter (values) {
    console.log (values)
    const newSearchObject = {
      ... values,
      pageIndex:1,
    };
    handleSetSearchObject (newSearchObject);
    await search ();
  }

  const [isOpenFilter, setIsOpenFilter] = useState (false);

  function handleCloseFilter () {
    if (isOpenFilter) {
      setIsOpenFilter (false);
    }
  }

  function handleOpenFilter () {
    if (!isOpenFilter) {
      setIsOpenFilter (true);
    }
  }

  function handleTogglePopupFilter () {
    if (isOpenFilter) handleCloseFilter ();
    else handleOpenFilter ();
  }

  const {isAdmin} = hrRoleUtilsStore;

  return (
      <Formik
          enableReinitialize
          initialValues={searchObject}
          onSubmit={handleFilter}
      >
        {({resetForm, values, setFieldValue, setValues}) => {

          return (
              <Form autoComplete="off">
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={12} md={6}>
                      {(isAdmin) && (<ButtonGroup
                              color="container"
                              aria-label="outlined primary button group"
                          >

                            <Button
                                startIcon={<AddIcon/>}
                                onClick={() => handleEditUser ()}
                            >
                              {t ("general.button.add")}
                            </Button>


                            {/* <Button
                                            disabled={listOnDelete?.length === 0}
                                            startIcon={<DeleteOutlineIcon />}
                                            onClick={handleDeleteList}
                                        >
                                            {t("general.button.delete")}
                                        </Button> */}
                          </ButtonGroup>
                      )}
                    </Grid>

                    <Grid item xs={12} md={6}>
                      <div className="flex justify-between align-center">
                        <Tooltip placement="top"
                                 title="Tìm kiếm theo tên tài khoản, email, tên nhân sư, mã nhân sự">
                          <GlobitsTextField
                              placeholder="Tìm kiếm theo tên tài khoản, email, tên nhân sư, mã nhân sự"
                              name="keyword"
                              variant="outlined"
                              notDelay
                          />
                        </Tooltip>

                        <ButtonGroup
                            className="filterButtonV4"
                            color="container"
                            aria-label="outlined primary button group"
                        >
                          <Button
                              startIcon={<SearchIcon className={``}/>}
                              className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                              type="submit"
                          >
                            Tìm kiếm
                          </Button>
                          {/* <Button
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc */}
                          {/* </Button> */}
                        </ButtonGroup>
                      </div>
                    </Grid>
                  </Grid>

                  {/* <AllowanceFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />
                             */}
                </Grid>
              </Form>
          );
        }}
      </Formik>
  );
}

export default memo (observer (UserToolbar));