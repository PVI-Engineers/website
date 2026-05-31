import { getSectionMedia } from './siteMedia'

const serviceButtons = [
  {
    label: 'Civil',
    path: '/civil',
    icon: 'mdi-bridge',
    intro:
      'Civil infrastructure design services for roads, urban corridors, utility integration, and long-term system reliability.',
    focusAreas: [
      'Road and corridor planning for urban and regional mobility',
      'Multi-utility integration with conflict-free alignment logic',
      'Constructable design outputs with clear phasing strategies',
    ],
    deliverables: [
      'Roadway geometry, grading, and utility coordination drawings',
      'Intersections and corridor optimization packages',
      'Concept-to-detailed civil engineering documentation',
      'Support for technical review and statutory submissions',
    ],
    children: [
      {
        label: 'Road and Highway Engineering',
        path: '/civil/road-and-highway-engineering',
        icon: 'mdi-road-variant',
        intro:
          'Engineering support for highways and arterial road systems with emphasis on safety, flow, and design consistency.',
        focusAreas: [
          'Horizontal and vertical alignment optimization',
          'Intersection and interchange performance enhancement',
          'Safety-oriented cross sections and access design',
        ],
        deliverables: [
          'Road plan, profile, and section drawings',
          'Intersection and junction redesign proposals',
          'Pavement and shoulder system recommendations',
          'Traffic movement improvement notes',
        ],
      },
      {
        label: 'Urban Infrastructure Design',
        path: '/civil/urban-infrastructure-design',
        icon: 'mdi-city-variant-outline',
        intro:
          'Integrated civil design for dense urban developments where roads, drainage, and public utilities must align seamlessly.',
        focusAreas: [
          'Street network design for mixed-use zones',
          'Public realm coordination with engineering layers',
          'Resilience planning for growth and redevelopment',
        ],
        deliverables: [
          'Urban street and utility integration plans',
          'Design standards and corridor typology sheets',
          'Phased implementation guidance for city programs',
          'Stakeholder-ready concept presentation packages',
        ],
      },
      {
        label: 'Utility and Corridor Coordination',
        path: '/civil/utility-and-corridor-coordination',
        icon: 'mdi-vector-polyline-edit',
        intro:
          'Conflict-free planning of underground and above-ground utility networks within transportation and civil corridors.',
        focusAreas: [
          'Cross-utility coordination and clash risk reduction',
          'Right-of-way optimization for future expansion',
          'Construction-friendly utility staging approaches',
        ],
        deliverables: [
          'Utility alignment and conflict matrices',
          'Corridor coordination and relocation plans',
          'Utility phasing and service continuity notes',
          'Approval-ready coordination drawings',
        ],
      },
    ],
  },
  {
    label: 'Survey',
    path: '/survey',
    icon: 'mdi-map-search-outline',
    intro:
      'Survey and geospatial services that provide precise base information for civil, road, and water design decisions.',
    focusAreas: [
      'High-accuracy terrain and feature capture',
      'Utility inventory and underground network referencing',
      'Geospatial analysis for planning and design optimization',
    ],
    deliverables: [
      'Topographic base sheets and contour models',
      'Utility mapping and control point reports',
      'GIS-backed spatial analysis outputs',
      'Survey data packages aligned with design teams',
    ],
    children: [
      {
        label: 'Topographic and Terrain Surveys',
        path: '/survey/topographic-and-terrain-surveys',
        icon: 'mdi-image-filter-hdr',
        intro:
          'Detailed terrain acquisition services to establish reliable design baselines for infrastructure projects.',
        focusAreas: [
          'Surface modeling and contour development',
          'Natural and built-feature capture',
          'Ground condition interpretation for planning',
        ],
        deliverables: [
          'Topographic survey plans and digital terrain models',
          'Control benchmark and contour documentation',
          'Site condition interpretation summaries',
          'Coordinate-ready data exports for design tools',
        ],
      },
      {
        label: 'Utility Mapping and Asset Capture',
        path: '/survey/utility-mapping-and-asset-capture',
        icon: 'mdi-layers-outline',
        intro:
          'Comprehensive mapping of existing assets and underground utility systems to reduce design and execution risk.',
        focusAreas: [
          'Utility tracing and corridor-level data capture',
          'Asset inventory development for planning teams',
          'Conflict visualization with proposed alignments',
        ],
        deliverables: [
          'Utility register and mapped asset layers',
          'As-is corridor utility plans',
          'Potential conflict and risk snapshots',
          'Survey-to-design integration datasets',
        ],
      },
      {
        label: 'GIS and Spatial Analytics',
        path: '/survey/gis-and-spatial-analytics',
        icon: 'mdi-map-legend',
        intro:
          'GIS-driven analytics to evaluate infrastructure alternatives, constraints, and impact zones across project areas.',
        focusAreas: [
          'Spatial suitability analysis for infrastructure planning',
          'Constraint mapping for phased implementation',
          'Data visualization for stakeholder decision support',
        ],
        deliverables: [
          'GIS dashboards and map outputs',
          'Planning constraint and opportunity layers',
          'Spatial scenario comparison reports',
          'Decision-support presentation graphics',
        ],
      },
    ],
  },
  {
    label: 'Water & Wastewater',
    path: '/water-wastewater',
    icon: 'mdi-water-sync',
    intro:
      'Comprehensive water and wastewater engineering for distribution reliability, drainage integration, and treatment planning.',
    focusAreas: [
      'Water network performance and resilience planning',
      'Wastewater collection and conveyance design',
      'Reuse-ready treatment and circular water strategies',
    ],
    deliverables: [
      'Water and sewer master planning documentation',
      'Hydraulic network models and optimization notes',
      'System expansion and rehabilitation recommendations',
      'Design documentation for implementation phases',
    ],
    children: [
      {
        label: 'Water Distribution Planning',
        path: '/water-wastewater/water-distribution-planning',
        icon: 'mdi-water-outline',
        intro:
          'Design and optimization of water supply networks to improve pressure, reliability, and operational stability.',
        focusAreas: [
          'Demand estimation and zoning strategy',
          'Pressure and flow balancing across networks',
          'Storage and pumping optimization options',
        ],
        deliverables: [
          'Water network layouts and hydraulic checks',
          'Storage and pumping station planning notes',
          'Pressure zone planning recommendations',
          'Short and long-term augmentation roadmaps',
        ],
      },
      {
        label: 'Wastewater Collection Networks',
        path: '/water-wastewater/wastewater-collection-networks',
        icon: 'mdi-pipe',
        intro:
          'Gravity and pumped wastewater system design that supports safe collection, conveyance, and future expansion.',
        focusAreas: [
          'Collection routing and invert level strategies',
          'Pumping and lift station integration',
          'Capacity checks for growth and peak events',
        ],
        deliverables: [
          'Collection network plans and profile sheets',
          'Pumping station planning packages',
          'Hydraulic capacity and overflow risk assessments',
          'Phased implementation support recommendations',
        ],
      },
      {
        label: 'Treatment and Reuse Planning',
        path: '/water-wastewater/treatment-and-reuse-planning',
        icon: 'mdi-recycle-variant',
        intro:
          'Planning support for treatment, reuse, and circular water systems aligned to environmental and operational goals.',
        focusAreas: [
          'Treatment capacity planning for staged growth',
          'Reuse potential mapping and demand alignment',
          'Effluent management and compliance planning',
        ],
        deliverables: [
          'Treatment and reuse concept alternatives',
          'Capacity planning and phasing reports',
          'Compliance-aligned planning notes',
          'Implementation strategy for reuse applications',
        ],
      },
    ],
  },
  {
    label: 'Bidding Documents',
    path: '/bidding-documents',
    icon: 'mdi-file-document-edit-outline',
    intro:
      'Bid-stage documentation support that transforms engineering intent into clear, auditable, and procurement-ready packages.',
    focusAreas: [
      'Precision documentation for tender readiness',
      'Clear scope definition and measurable deliverables',
      'Alignment between design, specification, and quantities',
    ],
    deliverables: [
      'Tender drawings, BOQs, and specification sets',
      'Contract package structuring support',
      'Technical clarifications and addendum notes',
      'Bid-stage compliance and review checklists',
    ],
    children: [
      {
        label: 'Tender Drawings and BOQ',
        path: '/bidding-documents/tender-drawings-and-boq',
        icon: 'mdi-file-table-box-multiple-outline',
        intro:
          'Preparation of bid drawings and quantity schedules that are detailed, coordinated, and execution-oriented.',
        focusAreas: [
          'Drawing package quality and consistency controls',
          'Itemized quantity derivation and traceability',
          'Design-to-BOQ alignment validation',
        ],
        deliverables: [
          'Tender-level drawing package sets',
          'Structured BOQ with technical references',
          'Quantity assumptions and basis documentation',
          'Revision logs for bid-stage updates',
        ],
      },
      {
        label: 'Technical Specifications',
        path: '/bidding-documents/technical-specifications',
        icon: 'mdi-notebook-edit-outline',
        intro:
          'Development of technical specification documents that define material, workmanship, and quality expectations.',
        focusAreas: [
          'Specification harmonization across disciplines',
          'Quality and compliance criteria integration',
          'Practical execution language for contractors',
        ],
        deliverables: [
          'Discipline-wise technical specification documents',
          'Material and workmanship requirement tables',
          'Quality control and acceptance criteria notes',
          'Specification clarifications for tender teams',
        ],
      },
      {
        label: 'Bid Evaluation Support',
        path: '/bidding-documents/bid-evaluation-support',
        icon: 'mdi-clipboard-check-multiple-outline',
        intro:
          'Structured support for technical bid review, compliance checks, and recommendation documentation.',
        focusAreas: [
          'Technical submission comparison frameworks',
          'Deviation identification and risk mapping',
          'Decision support aligned with project priorities',
        ],
        deliverables: [
          'Bid comparison and compliance matrices',
          'Technical deviation commentary notes',
          'Clarification questionnaire templates',
          'Recommendation-ready evaluation summaries',
        ],
      },
    ],
  },
]

const companyButtons = [
  {
    label: 'About Us',
    path: '/about-us',
    icon: 'mdi-domain',
    intro:
      'Learn about PVI ENGINEERS, our mission, values, leadership mindset, and commitment to modern infrastructure delivery.',
    focusAreas: [
      'Engineering excellence with delivery accountability',
      'Collaborative culture across disciplines',
      'Long-term resilience as a design principle',
    ],
    deliverables: [
      'Company profile and capability statements',
      'Leadership and organizational overview',
      'Quality and compliance commitment summary',
      'Mission and values framework',
    ],
    children: [
      {
        label: 'Leadership and Team',
        path: '/about-us/leadership-and-team',
        icon: 'mdi-account-supervisor-circle-outline',
        intro:
          'Meet the leadership and multidisciplinary team driving project delivery and technical quality.',
        focusAreas: [
          'Domain-led project governance',
          'Cross-functional collaboration practices',
          'Mentorship and capability development',
        ],
        deliverables: [
          'Leadership profiles and responsibility matrix',
          'Team structure overview',
          'Project ownership framework',
          'Specialized discipline coverage summary',
        ],
      },
      {
        label: 'Mission and Values',
        path: '/about-us/mission-and-values',
        icon: 'mdi-compass-outline',
        intro:
          'Understand the purpose and principles shaping how we design and deliver civil infrastructure projects.',
        focusAreas: [
          'Purpose-led engineering decisions',
          'Transparency and technical integrity',
          'Community and climate-conscious planning',
        ],
        deliverables: [
          'Mission statement and strategic priorities',
          'Core values and behavior commitments',
          'Client collaboration principles',
          'Sustainability and resilience intent summary',
        ],
      },
      {
        label: 'Quality and Compliance',
        path: '/about-us/quality-and-compliance',
        icon: 'mdi-check-decagram-outline',
        intro:
          'Our quality process and compliance approach ensure dependable engineering outputs across every project stage.',
        focusAreas: [
          'Document and drawing quality controls',
          'Standards alignment and design checks',
          'Risk-aware review workflows',
        ],
        deliverables: [
          'Quality control workflow summary',
          'Design review and sign-off checkpoints',
          'Compliance matrix templates',
          'Continuous improvement tracking approach',
        ],
      },
    ],
  },
  {
    label: 'Locations',
    path: '/locations',
    icon: 'mdi-map-marker-multiple-outline',
    intro:
      'Explore our operating locations, project presence, and collaborative network that supports regional delivery.',
    focusAreas: [
      'Regional project execution capabilities',
      'Local coordination with central technical oversight',
      'Partner-enabled expansion and response capacity',
    ],
    deliverables: [
      'Office and contact location references',
      'Region-wise project coverage summary',
      'Local support and collaboration pathways',
      'Operational response coordination points',
    ],
    children: [
      {
        label: 'Regional Offices',
        path: '/locations/regional-offices',
        icon: 'mdi-office-building-marker-outline',
        intro:
          'Information about primary and regional offices supporting project management and engineering coordination.',
        focusAreas: [
          'Regional client engagement touchpoints',
          'Local project coordination capabilities',
          'Engineering support availability by location',
        ],
        deliverables: [
          'Office address and contact references',
          'Regional support role definition',
          'Client communication channels',
          'Local escalation and response pathways',
        ],
      },
      {
        label: 'Project Presence Map',
        path: '/locations/project-presence-map',
        icon: 'mdi-map-marker-path',
        intro:
          'A footprint view of project activity zones and sectors where our engineering teams have delivered solutions.',
        focusAreas: [
          'Geographic spread of project engagements',
          'Sector-wise distribution across regions',
          'Growth corridors and future opportunities',
        ],
        deliverables: [
          'Regional project distribution highlights',
          'Sector footprint and engagement summary',
          'Opportunity and expansion indicators',
          'Client reference geography overview',
        ],
      },
      {
        label: 'Partner Network',
        path: '/locations/partner-network',
        icon: 'mdi-handshake-outline',
        intro:
          'Our strategic partner ecosystem helps us scale specialist support and deliver integrated infrastructure outcomes.',
        focusAreas: [
          'Consultant and specialist collaboration models',
          'Local execution partner coordination',
          'Shared quality and delivery standards',
        ],
        deliverables: [
          'Partner capability categories',
          'Coordination and governance model overview',
          'Joint delivery quality expectations',
          'Engagement and onboarding pathways',
        ],
      },
    ],
  },
  {
    label: 'News and Events',
    path: '/news-and-events',
    icon: 'mdi-newspaper-variant-outline',
    intro:
      'Stay updated on our latest announcements, participation in industry forums, and milestones across projects.',
    focusAreas: [
      'Project and company milestone communication',
      'Knowledge sharing through technical events',
      'Visibility into innovation and field learnings',
    ],
    deliverables: [
      'News updates and milestone briefs',
      'Event participation and schedule highlights',
      'Knowledge insights and announcement archive',
      'Media-ready summary notes',
    ],
    children: [
      {
        label: 'Company Announcements',
        path: '/news-and-events/company-announcements',
        icon: 'mdi-bullhorn-outline',
        intro:
          'Official updates related to company growth, partnerships, initiatives, and strategic milestones.',
        focusAreas: [
          'Business and capability expansion updates',
          'Partnership and collaboration announcements',
          'Service innovation milestones',
        ],
        deliverables: [
          'Announcement releases and update timelines',
          'Strategic milestone summaries',
          'Stakeholder communication references',
          'Archive-ready news statements',
        ],
      },
      {
        label: 'Industry Events',
        path: '/news-and-events/industry-events',
        icon: 'mdi-calendar-star',
        intro:
          'Highlights from conferences, workshops, and technical forums where our experts share practical insights.',
        focusAreas: [
          'Civil and infrastructure event participation',
          'Technical workshop speaking engagements',
          'Knowledge exchange with industry peers',
        ],
        deliverables: [
          'Event calendar and participation records',
          'Session focus and speaking topic summary',
          'Learning takeaways and follow-up notes',
          'Community engagement snapshots',
        ],
      },
      {
        label: 'Awards and Recognition',
        path: '/news-and-events/awards-and-recognition',
        icon: 'mdi-trophy-outline',
        intro:
          'Recognition highlights reflecting our commitment to quality engineering and high-impact infrastructure delivery.',
        focusAreas: [
          'Project excellence recognition',
          'Team and innovation acknowledgements',
          'Client trust and performance indicators',
        ],
        deliverables: [
          'Award and recognition highlights',
          'Recognition context and category summaries',
          'Team contribution acknowledgements',
          'Milestone timeline references',
        ],
      },
    ],
  },
  {
    label: 'Careers',
    path: '/careers',
    icon: 'mdi-account-tie-outline',
    intro:
      'Explore current openings, hiring steps, and work culture at PVI ENGINEERS as we grow our infrastructure teams.',
    focusAreas: [
      'Role-specific opportunities in civil, water, drainage, and survey',
      'Transparent hiring process with clear communication',
      'Long-term growth through mentorship and project exposure',
    ],
    deliverables: [
      'Live openings and role expectations',
      'Hiring stages and timeline overview',
      'Work culture and benefits information',
      'Application guidance for each role',
    ],
    children: [
      {
        label: 'Open Positions',
        path: '/careers/open-positions',
        icon: 'mdi-briefcase-search-outline',
        intro:
          'Current roles across engineering, design, project coordination, and technical documentation functions.',
        focusAreas: [
          'Role requirements and expected competencies',
          'Domain-specific hiring priorities',
          'Clear responsibility and growth tracks',
        ],
        deliverables: [
          'Live role listings with role summaries',
          'Qualification and experience expectations',
          'Application instructions and timelines',
          'Selection-stage communication references',
        ],
      },
      {
        label: 'Hiring Process',
        path: '/careers/hiring-process',
        icon: 'mdi-timeline-check-outline',
        intro:
          'Understand each recruitment stage from application review through final offer and onboarding.',
        focusAreas: [
          'Clear stage-by-stage selection process',
          'Expected timelines and communication points',
          'Interview and practical assessment guidance',
        ],
        deliverables: [
          'Hiring stage overview and expectations',
          'Assessment and interview readiness guidance',
          'Offer and onboarding process notes',
          'Frequently asked candidate clarifications',
        ],
      },
      {
        label: 'Work Culture and Benefits',
        path: '/careers/work-culture-and-benefits',
        icon: 'mdi-heart-multiple-outline',
        intro:
          'A collaborative engineering culture focused on learning, quality delivery, and meaningful project impact.',
        focusAreas: [
          'Team culture and collaboration practices',
          'Learning, upskilling, and growth support',
          'Recognition and wellbeing-oriented policies',
        ],
        deliverables: [
          'Culture principles and work model summary',
          'Learning and development opportunities',
          'Benefit and support structure overview',
          'Employee experience highlights',
        ],
      },
    ],
  },
  {
    label: 'Contact',
    path: '/contact',
    icon: 'mdi-email-outline',
    intro:
      'Reach our team for project discussions, partnership opportunities, service questions, and technical consultations.',
    focusAreas: [
      'Fast response to project-related inquiries',
      'Clear communication for proposal and scope alignment',
      'Reliable support channels for stakeholders',
    ],
    deliverables: [
      'Contact channels and escalation points',
      'Inquiry response process and SLA expectations',
      'Proposal submission pathway',
      'Client and partner communication support',
    ],
    children: [
      {
        label: 'General Inquiries',
        path: '/contact/general-inquiries',
        icon: 'mdi-message-text-outline',
        intro:
          'For service questions, capability clarifications, and initial consultation requests with our engineering team.',
        focusAreas: [
          'Service suitability and scope clarification',
          'Early-stage requirement understanding',
          'Direction to relevant technical teams',
        ],
        deliverables: [
          'Inquiry intake guidance and channels',
          'Expected response and follow-up timelines',
          'Pre-consultation information checklist',
          'Escalation route for urgent requirements',
        ],
      },
      {
        label: 'Business Proposals',
        path: '/contact/business-proposals',
        icon: 'mdi-handshake',
        intro:
          'Submit partnership and project proposals for strategic collaboration and long-term infrastructure programs.',
        focusAreas: [
          'Proposal submission and evaluation flow',
          'Scope alignment and technical review pathways',
          'Partnership model discussions',
        ],
        deliverables: [
          'Proposal intake and review stages',
          'Evaluation criteria overview',
          'Technical and commercial discussion channels',
          'Next-step decision communication model',
        ],
      },
      {
        label: 'Vendor Registration',
        path: '/contact/vendor-registration',
        icon: 'mdi-account-box-plus-outline',
        intro:
          'Information for consultants, suppliers, and service partners interested in joining our ecosystem.',
        focusAreas: [
          'Vendor profile and capability submission',
          'Prequalification and documentation checks',
          'Onboarding communication timelines',
        ],
        deliverables: [
          'Registration checklist and submission route',
          'Prequalification stage references',
          'Compliance and documentation expectations',
          'Vendor engagement communication model',
        ],
      },
    ],
  },
  {
    label: 'Our Work',
    path: '/our-work',
    icon: 'mdi-briefcase-outline',
    intro:
      'A showcase of our engineering outcomes across road systems, water infrastructure, and drainage resilience programs.',
    focusAreas: [
      'Portfolio depth across multiple infrastructure sectors',
      'Performance-driven design outcomes',
      'Implementation-aligned project documentation quality',
    ],
    deliverables: [
      'Project case highlights and outcomes',
      'Sector-wise portfolio overviews',
      'Delivery methodology and innovation references',
      'Client impact snapshots',
    ],
    children: [
      {
        label: 'Featured Road Projects',
        path: '/our-work/featured-road-projects',
        icon: 'mdi-highway',
        intro:
          'Representative road and corridor assignments that demonstrate mobility-focused engineering and execution readiness.',
        focusAreas: [
          'Corridor-level planning and redesign outcomes',
          'Intersection and safety enhancement programs',
          'Throughput and serviceability improvements',
        ],
        deliverables: [
          'Road project snapshot summaries',
          'Design challenge and solution narratives',
          'Measured impact and performance indicators',
          'Visual and technical portfolio references',
        ],
      },
      {
        label: 'Water and Drainage Projects',
        path: '/our-work/water-and-drainage-projects',
        icon: 'mdi-waves-arrow-up',
        intro:
          'Project examples focused on network reliability, flood resilience, and integrated water system performance.',
        focusAreas: [
          'Water distribution and storage optimization',
          'Drainage rehabilitation and flood mitigation',
          'Climate-responsive infrastructure planning',
        ],
        deliverables: [
          'Water and drainage project case summaries',
          'System performance and resilience outcomes',
          'Implementation strategy highlights',
          'Long-term maintenance and lifecycle notes',
        ],
      },
      {
        label: 'Project Delivery Approach',
        path: '/our-work/project-delivery-approach',
        icon: 'mdi-timeline-check-outline',
        intro:
          'How our teams structure data, design, review, and documentation for dependable project delivery.',
        focusAreas: [
          'Data-first engineering workflow design',
          'Scenario testing and technical quality control',
          'Client collaboration and milestone governance',
        ],
        deliverables: [
          'Delivery stage and governance framework',
          'Review and quality assurance checkpoints',
          'Documentation lifecycle summary',
          'Communication and reporting model',
        ],
      },
    ],
  },
  {
    label: 'Privacy Statement',
    path: '/privacy-statement',
    icon: 'mdi-shield-lock-outline',
    intro:
      'Our privacy commitments covering how we collect, use, and protect personal and business information.',
    focusAreas: [
      'Responsible data collection principles',
      'Transparent usage and retention practices',
      'Security safeguards for digital interactions',
    ],
    deliverables: [
      'Privacy policy overview and terms',
      'User rights and request guidance',
      'Data handling and retention principles',
      'Governance and compliance summary',
    ],
    children: [
      {
        label: 'Data Collection and Use',
        path: '/privacy-statement/data-collection-and-use',
        icon: 'mdi-database-eye-outline',
        intro:
          'Details on what data may be collected through our website and how that information is used responsibly.',
        focusAreas: [
          'Data categories and collection contexts',
          'Purpose-driven data usage boundaries',
          'Consent-aware communication practices',
        ],
        deliverables: [
          'Data category and usage transparency notes',
          'Retention and deletion guidance',
          'Access and correction request pathway',
          'Responsible usage statement',
        ],
      },
      {
        label: 'Cookie Policy',
        path: '/privacy-statement/cookie-policy',
        icon: 'mdi-cookie-outline',
        intro:
          'Information on cookie usage, session support functions, and user controls for browser-based tracking.',
        focusAreas: [
          'Essential and optional cookie categories',
          'Session, analytics, and preference handling',
          'User control and opt-out references',
        ],
        deliverables: [
          'Cookie type and purpose summary',
          'User control and browser setting guidance',
          'Session management information',
          'Policy update communication approach',
        ],
      },
      {
        label: 'Terms and Compliance',
        path: '/privacy-statement/terms-and-compliance',
        icon: 'mdi-gavel',
        intro:
          'Legal and compliance context associated with website use, data governance, and policy adherence expectations.',
        focusAreas: [
          'Policy governance and legal context',
          'Compliance responsibilities and standards',
          'Issue reporting and policy update process',
        ],
        deliverables: [
          'Terms-of-use and compliance references',
          'Governance and accountability summary',
          'Issue escalation and contact channels',
          'Version tracking and policy maintenance notes',
        ],
      },
    ],
  },
]

const attachMedia = (buttons) =>
  buttons.map((item) => {
    const media = getSectionMedia(item.path)

    return {
      ...item,
      heroImage: media.hero,
      galleryImages: media.gallery,
      children: item.children.map((child, index) => ({
        ...child,
        heroImage: media.gallery[index % media.gallery.length] ?? media.hero,
      })),
    }
  })

const serviceButtonsWithMedia = attachMedia(serviceButtons)
const companyButtonsWithMedia = attachMedia(companyButtons)

export { serviceButtonsWithMedia as serviceButtons, companyButtonsWithMedia as companyButtons }

export const mainButtons = [...serviceButtonsWithMedia, ...companyButtonsWithMedia]

const mainPages = mainButtons.map((item) => ({
  title: item.label,
  path: item.path,
  category: 'Main Section',
  parent: null,
  icon: item.icon,
  sectionPath: item.path,
  heroImage: item.heroImage,
  galleryImages: item.galleryImages,
  intro: item.intro,
  focusAreas: item.focusAreas,
  deliverables: item.deliverables,
  relatedLinks: item.children.map((child) => ({
    label: child.label,
    path: child.path,
  })),
}))

const childPages = mainButtons.flatMap((item) =>
  item.children.map((child) => ({
    title: child.label,
    path: child.path,
    category: `Inside ${item.label}`,
    parent: {
      label: item.label,
      path: item.path,
    },
    icon: child.icon,
    sectionPath: item.path,
    heroImage: child.heroImage,
    galleryImages: item.galleryImages,
    intro: child.intro,
    focusAreas: child.focusAreas,
    deliverables: child.deliverables,
    relatedLinks: [
      {
        label: `${item.label} Overview`,
        path: item.path,
      },
      ...item.children
        .filter((entry) => entry.path !== child.path)
        .map((entry) => ({
          label: entry.label,
          path: entry.path,
        })),
    ],
  })),
)

export const pageCatalog = [...mainPages, ...childPages]

export const pageMap = Object.fromEntries(pageCatalog.map((page) => [page.path, page]))
