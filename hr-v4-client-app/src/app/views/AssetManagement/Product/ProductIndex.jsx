/* eslint-disable react-hooks/exhaustive-deps */
import React, {memo, useEffect} from "react";
import {Button, ButtonGroup, Grid, Icon, IconButton, Popover, useMediaQuery, useTheme,} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import AddIcon from "@material-ui/icons/Add";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import {Form, Formik} from "formik";
import GlobitsTable from "app/common/GlobitsTable";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import {formatMoney} from "app/LocalFunction";
import {observer} from "mobx-react";
import DashboardIcon from "@material-ui/icons/Dashboard";
import PublishIcon from "@material-ui/icons/Publish";
import UpdateIcon from "@material-ui/icons/Update";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import ProductForm from "./ProductForm";

const ProductIndex = () => {
    const {t} = useTranslation();
    const {
        pageProduct,
        searchProduct,
        selectedDelete,
        onClosePopup,
        handleChangeFormSearch,
        onConfirmDeleteProduct,
        onOpenDeleteProduct,
        openFormProduct,
        pagingProduct,
        resetStore,
        selectedProduct
    } = useStore().assetManagementStore.productStore;

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const [anchorEl, setAnchorEl] = React.useState(null);
    const open = Boolean(anchorEl);
    const id = open ? "simple-popper" : undefined;
    const handleClick = (event) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    const columns = [
        {
            title: t("general.action"),
            render: (rowData) => (
                <>
                    <IconButton size="small" onClick={() => openFormProduct(rowData.id)}>
                        <Icon fontSize="small" color="primary">
                            edit
                        </Icon>
                    </IconButton>
                    <IconButton
                        size="small"
                        onClick={() => onOpenDeleteProduct(rowData.id)}
                    >
                        <Icon fontSize="small" color="secondary">
                            delete
                        </Icon>
                    </IconButton>
                </>
            ),
            maxWidth: "100px",
            align: "center",
        },
        {title: "Tên sảm phẩm", field: "name"},
        {title: "Mãcông cụ/ dụng cụ", field: "code"},
        {title: "Số seri", field: "serialNumber"},
        {
            title: "Giá",
            field: "price",
            render: (value) => formatMoney(value?.price),
        },
        {title: "Phòng ban quản lý", field: "department.name"},
    ];

    useEffect(() => {
        pagingProduct();
        return resetStore;
    }, []);

    async function handleFilter(values) {
        await handleChangeFormSearch(values)
    }

    return (
        <>
      {/* <Formik
                enableReinitialize
                initialValues={searchProduct}
                onSubmit={handleChangeFormSearch}
            >
                <Form className='content-index'>
                    <div className="index-breadcrumb">
                        <GlobitsBreadcrumb routeSegments={[{ name: "Công cụ/ dụng cụ" }, { name: "Công cụ/ dụng cụ" }]} />
                    </div>

                    <Grid className="index-card" container spacing={2}>
                        <Grid item md={6} xs={4}>
                            <Button
                                className="mr-16 btn btn-info d-inline-flex"
                                startIcon={<AddIcon />}
                                variant="contained"
                                onClick={() => openFormProduct()}
                            >
                                {t("general.button.add")}
                            </Button>
                        </Grid>
                        <Grid item md={6} xs={8}>
                            <GlobitsInputSearch name="keyword" />
                        </Grid>
                    </Grid>

                    <GlobitsTable
                        columns={columns}
                        data={pageProduct.content || []}
                        totalPages={pageProduct.totalPages}
                        totalElements={pageProduct.totalElements}
                        handleChangePage={(_, pageIndex) => handleChangeFormSearch({ pageIndex })}
                        setRowsPerPage={({ target }) => handleChangeFormSearch({ pageSize: target.value })}
                        pageSize={searchProduct.rowsPerPage}
                        page={searchProduct.pageIndex}
                    />
                </Form>
            </Formik> */}

          <div className="content-index">
              <div className="index-breadcrumb">
                  <GlobitsBreadcrumb
                      routeSegments={[
                          {name: t("navigation.category.title")},
                          {name: "Công cụ/ dụng cụ"},
                          {name: "Công cụ/ dụng cụ"},
                      ]}
                  />
              </div>
              <Grid className="index-card" container spacing={2}>
                  <Grid item xs={12}>
                      <Formik
                          enableReinitialize
                          initialValues={{keyword: ""}}
                          onSubmit={handleFilter}
                      >
                          {({resetForm, values, setFieldValue, setValues}) => {
                              return (
                                  <Form autoComplete="off">
                                      <Grid container spacing={2}>
                                          <Grid item xs={12} md={6}>
                                              <ButtonGroup
                                                  color="container"
                                                  aria-label="outlined primary button group"
                                              >
                                                  <Button startIcon={<AddIcon/>} onClick={() => openFormProduct()}>
                                                      {!isMobile && t("general.button.add")}
                                                  </Button>
                                                  {/* <Button
                disabled={true}
                startIcon={<DeleteOutlineIcon />}
                onClick={() => {}}
              >
                {!isMobile && t("general.button.delete")}
              </Button> */}
                                                  {/*<Button*/}
                                                  {/*    startIcon={<DashboardIcon/>}*/}
                                                  {/*    aria-describedby={id}*/}
                                                  {/*    type="button"*/}
                                                  {/*    onClick={handleClick}*/}
                                                  {/*>*/}
                                                  {/*    Khác*/}
                                                  {/*</Button>*/}
                                                  {/*<Popover*/}
                                                  {/*    id={id}*/}
                                                  {/*    open={open}*/}
                                                  {/*    anchorEl={anchorEl}*/}
                                                  {/*    onClose={handleClick}*/}
                                                  {/*    anchorOrigin={{*/}
                                                  {/*        vertical: "bottom",*/}
                                                  {/*        horizontal: "right",*/}
                                                  {/*    }}*/}
                                                  {/*    transformOrigin={{*/}
                                                  {/*        vertical: "top",*/}
                                                  {/*        horizontal: "right",*/}
                                                  {/*    }}*/}
                                                  {/*>*/}
                                                  {/*    <div className="menu-list-button">*/}
                                                  {/*        <div*/}
                                                  {/*            className="menu-item-button"*/}
                                                  {/*            style={{borderBottom: "1px solid #e0e0e0"}}*/}
                                                  {/*        >*/}
                                                  {/*            <UpdateIcon style={{fontSize: 16}}/> Cập nhật trạng thái*/}
                                                  {/*        </div>*/}
                                                  {/*        <div className="menu-item-button">*/}
                                                  {/*            <PublishIcon*/}
                                                  {/*                style={{fontSize: 16, transform: "rotate(180deg)"}}*/}
                                                  {/*            />{" "}*/}
                                                  {/*            Kết xuất danh sách*/}
                                                  {/*        </div>*/}
                                                  {/*        <div className="menu-item-button">*/}
                                                  {/*            <PublishIcon style={{fontSize: 16}}/> Import dữ liệu*/}
                                                  {/*        </div>*/}
                                                  {/*    </div>*/}
                                                  {/*</Popover>*/}
                                              </ButtonGroup>
                                          </Grid>
                                          <Grid item xs={12} md={6}>
                                              <div className="flex justify-between align-center">
                                                  <GlobitsTextField
                                                      placeholder="Tìm kiếm theo từ khóa..."
                                                      name="keyword"
                                                      variant="outlined"
                                                      notDelay
                                                  />
                                                  <ButtonGroup
                                                      className="filterButtonV4"
                                                      color="container"
                                                      aria-label="outlined primary button group"
                                                  >
                                                      <Button
                                                          startIcon={<SearchIcon/>}
                                                          className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                          type="submit"
                                                      >
                                                          Tìm kiếm
                                                      </Button>
                                                  </ButtonGroup>
                                              </div>
                                          </Grid>
                                      </Grid>
                                  </Form>
                              );
                          }}
                      </Formik>
                  </Grid>
                  <Grid item xs={12}>
                      <GlobitsTable
                          columns={columns}
                          data={pageProduct.content || []}
                          totalPages={pageProduct.totalPages}
                          totalElements={pageProduct.totalElements}
                          handleChangePage={(_, pageIndex) =>
                              handleChangeFormSearch({pageIndex})
                          }
                          setRowsPerPage={({target}) =>
                              handleChangeFormSearch({pageSize: target.value})
                          }
                          pageSize={searchProduct.rowsPerPage}
                          page={searchProduct.pageIndex}
                      />
                  </Grid>
              </Grid>

              <GlobitsConfirmationDialog
                  open={Boolean(selectedDelete)}
                  onConfirmDialogClose={() => onClosePopup()}
                  onYesClick={onConfirmDeleteProduct}
                  title={t("confirm_dialog.delete.title")}
                  text={t("confirm_dialog.delete.text")}
                  agree={t("confirm_dialog.delete.agree")}
                  cancel={t("confirm_dialog.delete.cancel")}
              />

              {selectedProduct && (<ProductForm/>)}
          </div>
        </>
    );
};

export default memo(observer(ProductIndex));
