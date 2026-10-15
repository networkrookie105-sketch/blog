import { describe, it, expect } from 'vitest'
import { profile, projects, notes } from '../../content/sample-profile'

describe('个人主页内容', () => {
  it('项目与示例文章链接都有唯一且完整的目标', () => {
    expect(new Set(projects.map((p) => p.slug)).size).toBe(projects.length)
    expect(new Set(notes.map((p) => p.slug)).size).toBe(notes.length)
    expect(projects.every((p) => p.context && p.contribution && p.challenge && p.result)).toBe(true)
    expect(notes.every((p) => p.paragraphs.length > 0)).toBe(true)
    expect(profile.email.endsWith('@example.com')).toBe(true)
  })
})
