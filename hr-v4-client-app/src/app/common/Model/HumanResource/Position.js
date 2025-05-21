export class Position {
  id = null;
  code = null;
  name = null;
  description = null;
  status = null; // 0 - Khong duoc su dung; 1 - Dang duoc su dung
  title = null;  // Chức danh
  department = null;
  staff = null;
  /*
   * Là position chính của Staff này
   */
  isMain = null;
  isConcurrent = null;
  isTemporary = null; // Là tạm thời = tuyển lọc
  mainOrConcurrent = null;
  relationships = [];

  constructor () {
    this.isMain = true;
    this.isConcurrent = false;
    this.isTemporary = false;
  }
}