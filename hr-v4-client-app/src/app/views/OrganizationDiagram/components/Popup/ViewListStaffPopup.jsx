import { DialogContent, Grid } from "@material-ui/core";
import GlobitsPopup from "app/common/GlobitsPopup";
import { pagingPosition } from "app/views/Position/PositionService";
import React from "react";
import { toast } from "react-toastify";
import { useFormikContext } from "formik";

export default function ViewListStaffPopup ({open, handleClose, node}) {
  const {values} = useFormikContext ();
  const matchedNode = values?.orgChartData?.nodes.find (n => n?.objectId === node?.data?.objectId);
  const departmentId = matchedNode?.objectId;
  const departmentCode = matchedNode?.code;

  const [listPositions, setListPositions] = React.useState ([]);
  React.useEffect (() => {
    if (departmentId || departmentCode) {
      const payload = {
        departmentId,
        isExportExcel:true,
        departmentCode
      }
      pagingPosition (payload)
          .then (({data}) => {
            setListPositions (data?.content || []);
          })
          .catch ((err) => {
            console.error (err);
            toast.error ("Có lỗi xảy ra vui lòng thử lại");
          });
    }
  }, [departmentId, departmentCode]);

  return (
      <GlobitsPopup
          popupId='edit-node-popup'
          scroll={"body"}
          size='xs'
          open={open}
          noDialogContent
          title={`Danh sách Vị trí`}
          onClosePopup={handleClose}>
        <div className='dialog-body'>
          <DialogContent className='p-12'>
            <Grid container spacing={2}>
              {listPositions?.length > 0? (
                  listPositions.map ((position, index) => {
                    let className = "flex flex-middle";
                    if (index % 2) {
                      className += " bg-slate-200";
                    }
                    return (
                        <Grid item xs={12} className={className}>
                          <div>
                            <h5 className='text-dark-green mb-0'>{position?.name}</h5>
                            <span>{position?.staff?.displayName || "Vacant"}</span>
                          </div>
                        </Grid>
                    );
                  })
              ) : (
                  <Grid item xs={12}>
                    Không có dữ liệu
                  </Grid>
              )}
            </Grid>
          </DialogContent>
        </div>

        {/* <div className="dialog-footer py-8">
        <DialogActions className="p-0">
          <div className="flex flex-space-between flex-middle">
            <Button
              startIcon={<BlockIcon />}
              className="btn btn-secondary d-inline-flex mr-8"
              onClick={() => handleClose()}
            >
              Huỷ
            </Button>
            <Button
              startIcon={<SaveIcon />}
              className="btn btn-primary d-inline-flex"
              type="submit"
              disabled={isSubmitting}
            >
              Cập nhật
            </Button>
          </div>
        </DialogActions>
      </div> */}
      </GlobitsPopup>
  );
}
