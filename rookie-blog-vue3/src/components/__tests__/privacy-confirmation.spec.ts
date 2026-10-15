import { describe, it, expect, vi } from 'vitest'
import { ElMessageBox } from 'element-plus'
import { confirmPersonalDisclosure } from '../../composables/useSiteSection'
vi.mock('element-plus', () => ({ ElMessageBox: { confirm: vi.fn() } }))
describe('公开范围保存确认', () => {
  it('取消公开会保留编辑，关闭和未扩大范围的保存无需确认', async () => {
    vi.mocked(ElMessageBox.confirm).mockRejectedValueOnce('cancel')
    expect(
      await confirmPersonalDisclosure(
        'settings',
        { personalHomepagePublic: true },
        { personalHomepagePublic: false }
      )
    ).toBe(false)
    expect(ElMessageBox.confirm).toHaveBeenCalledTimes(1)
    expect(
      await confirmPersonalDisclosure(
        'settings',
        { personalHomepagePublic: false },
        { personalHomepagePublic: true }
      )
    ).toBe(true)
    expect(ElMessageBox.confirm).toHaveBeenCalledTimes(1)
  })
  it('恢复联系方式展示时明确说明将公开的字段', async () => {
    vi.mocked(ElMessageBox.confirm).mockResolvedValueOnce({ action: 'confirm', value: '' } as any)
    expect(
      await confirmPersonalDisclosure(
        'profile',
        { contactVisibility: { email: true } },
        { contactVisibility: { email: false } }
      )
    ).toBe(true)
    expect(ElMessageBox.confirm).toHaveBeenLastCalledWith(
      expect.stringContaining('邮箱'),
      '确认公开范围',
      expect.any(Object)
    )
  })
})
