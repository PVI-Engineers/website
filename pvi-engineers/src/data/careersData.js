const openings = [
  {
    id: 'PVI-RD-101',
    role: 'Senior Road Design Engineer',
    type: 'Full-time',
    location: 'Bengaluru, India',
    country: 'INDIA',
    workModel: 'Hybrid',
    department: 'Civil Design',
    experience: '7+ years',
    reportingTo: 'Head of Road Engineering',
    openingCount: 2,
    postedOn: 'May 2026',
    compensation: 'Market-aligned package based on experience and technical depth',
    noticePeriod: 'Immediate to 45 days preferred',
    summary:
      'Lead corridor and highway design packages for major infrastructure programs, including geometry, coordination, and technical reviews.',
    responsibilities: [
      'Lead horizontal and vertical alignment design across multi-km road corridors',
      'Review intersections, interchanges, cross sections, and constructability aspects',
      'Drive drawing quality, quantity documentation, and design review closure',
      'Coordinate with survey, drainage, and utilities teams for integrated delivery',
      'Support client reviews, technical submissions, and authority clarifications',
    ],
    basicQualifications: [
      'B.E./B.Tech in Civil Engineering',
      '7+ years of relevant experience in road or highway engineering',
      'Strong expertise in corridor geometry and junction planning',
      'Experience handling design QA/QC and multidisciplinary coordination',
      'Strong written and verbal communication for stakeholder interactions',
    ],
    preferredQualifications: [
      'Experience in urban mobility improvement or smart-city corridor projects',
      'Exposure to bid documentation and design-stage risk review',
      'Mentoring experience for junior engineers and design teams',
    ],
    tools: [
      'Civil 3D or equivalent corridor design platform',
      'AutoCAD and drawing standards workflows',
      'MS Office for technical reporting and review logs',
    ],
    benefits: [
      'Performance-linked growth opportunities',
      'Structured technical mentorship and leadership access',
      'Insurance and leave benefits as per company policy',
      'Cross-domain project exposure',
    ],
  },
  {
    id: 'PVI-WW-204',
    role: 'Water Systems Analyst',
    type: 'Full-time',
    location: 'Mumbai, India',
    country: 'INDIA',
    workModel: 'On-site',
    department: 'Water & Wastewater',
    experience: '3+ years',
    reportingTo: 'Head of Water & Drainage',
    openingCount: 1,
    postedOn: 'May 2026',
    compensation: 'Competitive fixed pay with role-based incentives',
    noticePeriod: 'Up to 30 days preferred',
    summary:
      'Develop hydraulic planning outputs for water supply systems with strong focus on reliability, demand balancing, and operational performance.',
    responsibilities: [
      'Build and validate hydraulic network models for distribution systems',
      'Develop demand scenarios and pressure-zone recommendations',
      'Support storage and pumping strategy studies',
      'Prepare clear technical reports and design notes for client review',
      'Coordinate with civil and drainage teams for integrated planning',
    ],
    basicQualifications: [
      'B.E./B.Tech in Civil or related engineering discipline',
      '3+ years of water infrastructure planning or design experience',
      'Hands-on understanding of flow, pressure, and demand analysis',
      'Strong reporting and documentation discipline',
      'Ability to work in collaborative multi-team environments',
    ],
    preferredQualifications: [
      'Experience in municipal water upgrade programs',
      'Exposure to industrial township utility planning',
      'Understanding of NRW reduction and system efficiency strategies',
    ],
    tools: [
      'Hydraulic modeling tools (project-specific)',
      'GIS-compatible planning workflows',
      'AutoCAD and spreadsheet-based analysis reporting',
    ],
    benefits: [
      'Role-specific learning tracks',
      'Direct exposure to city-scale infrastructure planning',
      'Structured performance feedback cycles',
      'Supportive engineering team culture',
    ],
  },
  {
    id: 'PVI-DR-309',
    role: 'Drainage Design Engineer',
    type: 'Full-time',
    location: 'Houston, USA',
    country: 'USA',
    workModel: 'Hybrid',
    department: 'Drainage & Flood Resilience',
    experience: '4+ years',
    reportingTo: 'Senior Drainage Lead',
    openingCount: 1,
    postedOn: 'May 2026',
    compensation: 'Experience-based compensation with review-linked increments',
    noticePeriod: 'Immediate to 30 days preferred',
    summary:
      'Engineer urban drainage and stormwater solutions, including flood resilience planning and implementation-ready design documentation.',
    responsibilities: [
      'Prepare stormwater layouts, sizing calculations, and drainage notes',
      'Support flood-risk mapping and mitigation alternatives',
      'Coordinate with road, utility, and survey teams for complete design solutions',
      'Contribute to BOQ and tender-stage technical documentation',
      'Support review meetings and issue closure with clients',
    ],
    basicQualifications: [
      'B.E./B.Tech in Civil Engineering',
      '4+ years of drainage or stormwater engineering experience',
      'Strong understanding of hydrology fundamentals and hydraulic behavior',
      'Ability to develop practical, constructable layouts and reports',
      'Good cross-team communication and delivery ownership',
    ],
    preferredQualifications: [
      'Experience in climate-resilient urban drainage programs',
      'Exposure to flood-prone city improvement projects',
      'Hands-on support in bid documentation and review cycles',
    ],
    tools: [
      'Hydrology/hydraulic analysis tools (project dependent)',
      'AutoCAD and drawing documentation workflows',
      'GIS-based flood mapping support tools',
    ],
    benefits: [
      'Meaningful city-impact project work',
      'Learning support for resilience and sustainability methods',
      'Balanced hybrid work model',
      'Clear progression pathways',
    ],
  },
  {
    id: 'PVI-SG-415',
    role: 'Survey & GIS Coordinator',
    type: 'Full-time',
    location: 'Dallas, USA',
    country: 'USA',
    workModel: 'On-site',
    department: 'Survey & Geospatial',
    experience: '3+ years',
    reportingTo: 'Survey & GIS Lead',
    openingCount: 1,
    postedOn: 'May 2026',
    compensation: 'Competitive package based on domain and coordination capability',
    noticePeriod: 'Up to 30 days preferred',
    summary:
      'Coordinate field-to-design survey and GIS workflows to ensure accurate base data for infrastructure engineering teams.',
    responsibilities: [
      'Manage survey data intake, QA checks, and issue tracking',
      'Prepare GIS layers and map outputs for planning teams',
      'Support utility and corridor mapping integration',
      'Coordinate handoff between survey, design, and project teams',
      'Maintain data traceability and documentation standards',
    ],
    basicQualifications: [
      'B.E./Diploma or equivalent relevant technical qualification',
      '3+ years of survey/GIS workflow experience',
      'Strong discipline in data quality and version control',
      'Comfort working with engineering teams across functions',
      'Good reporting and communication capability',
    ],
    preferredQualifications: [
      'Experience in infrastructure corridor survey projects',
      'Exposure to utility mapping and geospatial asset capture',
      'Ability to support planning visualizations for stakeholder review',
    ],
    tools: [
      'GIS platforms and mapping workflows',
      'CAD base plan coordination',
      'Spreadsheet and documentation tracking tools',
    ],
    benefits: [
      'Exposure to road, water, and drainage project ecosystems',
      'Structured team collaboration framework',
      'Learning support for digital engineering workflows',
      'Performance-linked career progression',
    ],
  },
]

const hiringFlow = [
  {
    step: 'Step 1',
    title: 'Application Review',
    detail: 'Profile shortlisting based on role fit, domain experience, and project relevance.',
  },
  {
    step: 'Step 2',
    title: 'Technical Interview',
    detail: 'Discussion with domain leads around project exposure, design judgment, and problem-solving approach.',
  },
  {
    step: 'Step 3',
    title: 'Practical Assessment',
    detail: 'Role-specific exercise or case conversation to evaluate delivery readiness and communication clarity.',
  },
  {
    step: 'Step 4',
    title: 'Leadership Round & Offer',
    detail: 'Final interaction on team alignment, growth path, compensation, and onboarding timeline.',
  },
]

const cultureHighlights = [
  {
    icon: 'mdi-rocket-launch-outline',
    title: 'High-Impact Infrastructure Work',
    detail: 'Contribute to projects in roads, water systems, drainage, and survey that shape real communities.',
  },
  {
    icon: 'mdi-account-group-outline',
    title: 'Collaborative Engineering Teams',
    detail: 'Work with multidisciplinary specialists in an environment built on transparency and shared ownership.',
  },
  {
    icon: 'mdi-school-outline',
    title: 'Continuous Technical Learning',
    detail: 'Upskilling support across digital engineering tools, project delivery methods, and leadership capabilities.',
  },
  {
    icon: 'mdi-chart-line-variant',
    title: 'Clear Growth Pathways',
    detail: 'Structured progression through performance feedback, mentorship, and meaningful responsibilities.',
  },
]

const getOpeningById = (jobId) =>
  openings.find((item) => item.id.toLowerCase() === String(jobId ?? '').toLowerCase()) ?? null

export { openings, hiringFlow, cultureHighlights, getOpeningById }
